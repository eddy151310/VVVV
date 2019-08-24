package com.ahdi.lib.utils.security;


import com.ahdi.lib.utils.cryptor.aes.AESHelper;
import com.ahdi.lib.utils.cryptor.EncryptUtil;
import com.ahdi.lib.utils.utils.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class SecurityCodeBody implements CodeBody {

    int rsaKeyID;
    byte[] rsaPubKey;
    byte[] aesKey;

    public SecurityCodeBody(int rsaKeyId, byte[] rsaPubKey) {
        this.rsaKeyID = rsaKeyId;
        this.rsaPubKey = rsaPubKey;
//        this.aesKey = new byte[16];//  生成一个随机字符串
        this.aesKey = EncryptUtil.decryptBASE64(AESHelper.getSecretKey());
    }

    @Override
    public byte[] encode(byte[] bytes) throws ClientException {
//        byte[0]+toBytes(id)+toBytes(lengthOf(enData))+ enData+ toBytes(lengthOf(key))+key+ toBytes(lengthOf(sign))+sign
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        try {
            out.write(0);// flag位
            BytesUtils.writeInt(rsaKeyID, out);
            byte[] data = bytes;
            byte[] key = aesKey;
            byte[] sign = SecurityUtils.md5Sign(merge(data, key));
            BytesUtils.writeWithLength(SecurityUtils.aesEncode(key, data), out);
            BytesUtils.writeWithLength(SecurityUtils.rsaEncode(this.rsaPubKey, key), out);
            BytesUtils.writeWithLength(sign, out);
        } catch (Exception e) {
            // 加密请求数据时发生了错误
            throw new ClientException(ClientException.EX_INVALID_REQ,
                    "An error occurred when encryption request data", e);
        }
        return out.toByteArray();
    }

    @Override
    public byte[] decode(byte[] bytes) {
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        int code = input.read();
        if (code != 0) {
            LogUtil.e("tag","Exception @ SecurityCodeBody code: " + code);
            throw new ClientException(code);
        }
        byte[] toDecode;
        byte[] sign;
        try {
            toDecode = BytesUtils.readWithLength(input);
            sign = BytesUtils.readWithLength(input);//OOM
        } catch (IOException e) {
            // 解析响应数据时出错(1)
            throw new ClientException(ClientException.EX_INVALID_RESP,
                    "An error occurred while parsing the response data (1)", e);
        }
        byte[] body;
        try {
            body = SecurityUtils.aesDecode(aesKey, toDecode);
        } catch (Exception e) {
            // 解析响应数据时出错(2)
            throw new ClientException(ClientException.EX_INVALID_RESP,
                    "An error occurred while parsing the response data (1)", e);
        }
        byte[] mysign;
        try {
            mysign = SecurityUtils.md5Sign(merge(body, this.aesKey));
        } catch (Exception e) {
            // 解析响应数据时出错(3),做MD5签名失败
            throw new ClientException(ClientException.EX_INVALID_RESP,
                    "An error occurred while parsing the response data (3), do the MD5 signature failure", e);
        }
        if (!Arrays.equals(sign, mysign)) {
            // 解析响应数据时出错(4),签名不一致
            throw new ClientException(ClientException.EX_INVALID_RESP,
                    "An error occurred while parsing the response data (4), a signature does not agree");
        }
        return body;
    }

    static byte[] merge(byte[] data, byte[] md5key) {
        if (data == null) {
            data = md5key;
        } else {
            int length = data.length;
            data = Arrays.copyOf(data, length + md5key.length);
            System.arraycopy(md5key, 0, data, length, md5key.length);
        }
        return data;
    }
}

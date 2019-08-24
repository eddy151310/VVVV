package com.ahdi.lib.utils.cryptor.aes;

import com.ahdi.lib.utils.config.ConfigHelper;
import com.ahdi.lib.utils.cryptor.EncryptUtil;
import com.ahdi.lib.utils.cryptor.rsa.RSAConfig;
import com.ahdi.lib.utils.cryptor.rsa.RSAHelper;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.StringUtil;

public class AesKeyCryptor {

    private static final String TAG = AesKeyCryptor.class.getSimpleName();

    /**
     * 用RSA公钥对AES私钥加密
     * 后续返回的服务端数据，要用AES私钥解密
     * <p>
     * <p>
     * 格式:80|0|xxxxx
     *
     * @return
     */
    public static String encodeAESPrivateKey() {
        int keyNo = RSAHelper.PLAT_PUBLIC_KEY_ID;       // "80"
        String publicKey = RSAHelper.PLAT_PUBLIC_KEY;   // rsa 公钥
        String rsaData = "";
        String aesKey = AESHelper.getSecretKey();
        LogUtil.d(TAG, "AES private Key is:" + aesKey);
        AppGlobalUtil.getInstance().setAesPrivateKey(aesKey);
        try {
            rsaData = RSAHelper.encryptByPublicKey(aesKey, publicKey);
        } catch (Exception e) {
            LogUtil.e(TAG, "encodePwd exception:" + e.toString());
            rsaData = AESHelper.getSecretKey();
        }
        return keyNo + "|0|" + rsaData;
    }

    /**
     * 用aes解密，后台数据给出的是base64后的数据
     *
     * @param data
     * @return
     */
    public static String decodeData(String data) {
        byte[] baseData = EncryptUtil.decryptBASE64(data);
        String payParamDecrypt = "";
        try {
            payParamDecrypt = new String(AESHelper.decrypt(baseData, AppGlobalUtil.getInstance().getAesPrivateKey().getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payParamDecrypt;
    }

    /**
     * @param pwd 需要加密的密码明文
     * @return 经过rsa加密的密码
     */
    public static String encodePayPwd(String pwd) {
        return encodePayPwd(pwd, StringUtil.getStringRandom(16));
    }

    /**
     * [keyNo]|[ Ver]|rsa("明文数据"，服务端公钥)
     * keyNo：密钥序号，登录密码为1，支付密码为2
     * Ver=2时：
     * 定义len00(x)=获取字符串x转为长度的十进制数字,固定两个字符，不足两个字符时高位补零，len(null)=0；
     * 明文数据=len00(密码明文)+密码明文+len00(密码Token)+密码Token+len00(其他数据)+其他数据
     * 其中密码Token和 "其他数据" 均为可选数据
     * <p>
     * 2|2|base64(rsa(061234560016qwrqwepoiu8mkjih))
     *
     * @param pwd    需要加密的密码明文
     * @param random 随机key, 客户端随机生成(8~16位)
     * @return 经过rsa加密的密码
     */
    public static String encodePayPwd(String pwd, String random) {
        String keyNo = ConfigHelper.PAY_PASSWORD_RSA_ID_PREFIX;

        int pwdLen = pwd.length();
        String temp;
        if (pwdLen < 10) {
            temp = "0" + pwdLen;
        } else {
            temp = "" + pwdLen;
        }

        int randomLen = random.length();
        String temp2;
        if (randomLen < 10) {
            temp2 = "0" + randomLen;
        } else {
            temp2 = "" + randomLen;
        }

        String data = temp + pwd + "00" + temp2 + random;
        String key = RSAConfig.instance().getPublicKey_pwd_pay();
        String rsaPwd = "";

        try {
            rsaPwd = RSAHelper.encryptByPublicKey(data, key);
        } catch (Exception e) {
            LogUtil.e(TAG, "encodePayPwd exception:" + e.toString());
            rsaPwd = pwd;
        }
        return keyNo + rsaPwd;
    }

    public static String encodeLoginPwd(String pwd) {
        String keyNo = ConfigHelper.LOGIN_PASSWORD_RSA_ID_PREFIX;
        String key = RSAConfig.instance().getPublicKey_pwd_login();
        String rsaPwd = "";
        try {
            rsaPwd = RSAHelper.encryptByPublicKey(pwd, key);
        } catch (Exception e) {
            LogUtil.e(TAG, "encodePayPwd exception:" + e.toString());
            rsaPwd = pwd;
        }
        return keyNo + rsaPwd;
    }

    public static String encodeClientKey(String clientKey) {
        String key = RSAConfig.instance().getPublicKey_pwd_pay();
        String rsaPwd = "";
        try {
            rsaPwd = RSAHelper.encryptByPublicKey(clientKey, key);
        } catch (Exception e) {
            LogUtil.e(TAG, "encodePayPwd exception:" + e.toString());
            rsaPwd = clientKey;
        }
        return rsaPwd;
    }
}

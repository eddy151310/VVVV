package com.ahdi.lib.utils.cryptor.rsa;


import com.ahdi.lib.utils.config.ConfigHelper;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.cryptor.EncryptUtil;
import com.ahdi.lib.utils.cryptor.PKCS1EncodedKeySpec;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;


public class RSAHelper {
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM_MD5 = "MD5withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA = "SHA256withRSA";
    public static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PLAT_PUBLIC_KEY = ConfigHelper.BCA_RSA_PUBLIC_KEY;
    public static final int PLAT_PUBLIC_KEY_ID = ConfigHelper.BCA_RSA_PUBLIC_KEY_ID;


    /**
     * 生成公钥和私钥
     *
     * @throws NoSuchAlgorithmException
     */
    public static Map<String, Object> getKeys() throws NoSuchAlgorithmException {
        HashMap<String, Object> map = new HashMap<>();
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        PrivateKeyInfo pki = PrivateKeyInfo.getInstance(privateKey.getEncoded());
        map.put(PUBLIC_KEY, publicKey);
        map.put(PRIVATE_KEY, pki);
        return map;
    }

    /**
     * 使用模和指数生成RSA公钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA
     * /None/NoPadding】
     *
     * @param modulus  模
     * @param exponent 指数
     * @return
     */
    public static RSAPublicKey getPublicKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用模和指数生成RSA私钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA
     * /None/NoPadding】
     *
     * @param modulus  模
     * @param exponent 指数
     * @return
     */
    public static RSAPrivateKey getPrivateKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, RSAPublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        // 模长
        int key_len = publicKey.getModulus().bitLength() / 8;

        // 加密数据长度 <= 模长-11
        String[] datas = splitString(data, key_len - 11);
        String mi = "";
        //如果明文长度大于模长-11则要分组加密
        for (String s : datas) {
            mi += bcd2Str(cipher.doFinal(s.getBytes()));
        }
        return mi;
    }

    /**
     * 私钥解密
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey)
            throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        //模长
        int key_len = privateKey.getModulus().bitLength() / 8;

        byte[] bytes = data.getBytes();
        byte[] bcd = ASCII_To_BCD(bytes, bytes.length);

        //如果密文长度大于模长则要分组解密
        String ming = "";
        byte[][] arrays = splitArray(bcd, key_len);
        for (byte[] arr : arrays) {
            ming += new String(cipher.doFinal(arr));
        }
        return ming;
    }

    public static String encryptByPrivateKey(String data, RSAPrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        // 模长
        int key_len = privateKey.getModulus().bitLength() / 8;

        // 加密数据长度 <= 模长-11
        String[] datas = splitString(data, key_len - 11);
        String mi = "";
        //如果明文长度大于模长-11则要分组加密
        for (String s : datas) {
            mi += bcd2Str(cipher.doFinal(s.getBytes()));
        }
        return mi;
    }

    public static String decryptByPublicKey(String data, RSAPublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        // 模长
        int key_len = publicKey.getModulus().bitLength() / 8;

        byte[] bytes = data.getBytes();
        byte[] bcd = ASCII_To_BCD(bytes, bytes.length);

        //如果密文长度大于模长则要分组解密
        String ming = "";
        byte[][] arrays = splitArray(bcd, key_len);
        for (byte[] arr : arrays) {
            ming += new String(cipher.doFinal(arr));
        }
        return ming;
    }

    public static String encryptByPublicModulusAndExponent(String data, String modulus, String exponent) throws Exception {
        RSAPublicKey publicKey = RSAHelper.getPublicKey(modulus, exponent);
        return encryptByPublicKey(data, publicKey);
    }

    public static String decryptByPublicModulusAndExponent(String data, String modulus, String exponent) throws Exception {
        RSAPublicKey publicKey = RSAHelper.getPublicKey(modulus, exponent);
        return decryptByPublicKey(data, publicKey);
    }

    /**
     * ASCII码转BCD码
     */
    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = asc_to_bcd(ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }

    public static byte asc_to_bcd(byte asc) {
        byte bcd;

        if ((asc >= '0') && (asc <= '9'))
            bcd = (byte) (asc - '0');
        else if ((asc >= 'A') && (asc <= 'F'))
            bcd = (byte) (asc - 'A' + 10);
        else if ((asc >= 'a') && (asc <= 'f'))
            bcd = (byte) (asc - 'a' + 10);
        else
            bcd = (byte) (asc - 48);
        return bcd;
    }

    /**
     * BCD转字符串
     */
    public static String bcd2Str(byte[] bytes) {
        char temp[] = new char[bytes.length * 2], val;

        for (int i = 0; i < bytes.length; i++) {
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

            val = (char) (bytes[i] & 0x0f);
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        }
        return new String(temp);
    }

    /**
     * 拆分字符串
     */
    public static String[] splitString(String string, int len) {
        int x = string.length() / len;
        int y = string.length() % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        String[] strings = new String[x + z];
        String str = "";
        for (int i = 0; i < x + z; i++) {
            if (i == x + z - 1 && y != 0) {
                str = string.substring(i * len, i * len + y);
            } else {
                str = string.substring(i * len, i * len + len);
            }
            strings[i] = str;
        }
        return strings;
    }

    /**
     * 拆分数组
     */
    public static byte[][] splitArray(byte[] data, int len) {
        int x = data.length / len;
        int y = data.length % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        byte[][] arrays = new byte[x + z][];
        byte[] arr;
        for (int i = 0; i < x + z; i++) {
            arr = new byte[len];
            if (i == x + z - 1 && y != 0) {
                System.arraycopy(data, i * len, arr, 0, y);
            } else {
                System.arraycopy(data, i * len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }

    public static String signWithMD5(String data, String privateKey) throws Exception {
        return sign(data, privateKey, SIGNATURE_ALGORITHM_MD5);
    }

    public static String signWithSHA(String data, String privateKey) throws Exception {
        return sign(data, privateKey, SIGNATURE_ALGORITHM_SHA);
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       加密数据
     * @param privateKey 私钥
     * @return
     * @throws Exception
     */
    private static String sign(String data, String privateKey, String signatureType) throws Exception {
        // 解密由base64编码的私钥
        byte[] keyBytes = EncryptUtil.decryptBASE64(privateKey);

        // 构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        // 取私钥匙对象
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(signatureType);
        signature.initSign(priKey);
        signature.update(data.getBytes("UTF-8"));

        return EncryptUtil.encryptBASE64(signature.sign());
    }

    /**
     * 校验数字签名
     *
     * @param data      加密数据
     * @param publicKey 公钥
     * @param sign      数字签名
     * @return 校验成功返回true 失败返回false
     * @throws Exception
     */
    public static boolean verify(String data, String publicKey, String sign)
            throws Exception {

        // 解密由base64编码的公钥
        byte[] keyBytes = EncryptUtil.decryptBASE64(publicKey);

        // 构造X509EncodedKeySpec对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        // 取公钥匙对象
        PublicKey pubKey = keyFactory.generatePublic(keySpec);

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_SHA);
        signature.initVerify(pubKey);
        signature.update(data.getBytes("UTF-8"));

        // 验证签名是否正常
        return signature.verify(EncryptUtil.decryptBASE64(sign));
    }

    /**
     * 解密<br>
     * 用私钥解密 http://www.5a520.cn http://www.feng123.com
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String data, String key)
            throws Exception {
        // 对密钥解密
        byte[] keyBytes = EncryptUtil.decryptBASE64(key);

        // 取得私钥
        PKCS1EncodedKeySpec privateKeyPKCS1 = new PKCS1EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 提取私钥
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeyPKCS1.getKeySpec());

        // 对数据解密
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);//FIXME:这里的初始化值可能是不对的
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return new String(cipher.doFinal(EncryptUtil.decryptBASE64(data)));
    }

    /**
     * 解密<br>
     * 用公钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String decryptByPublicKey(String data, String key)
            throws Exception {
        // 对密钥解密
        byte[] keyBytes = EncryptUtil.decryptBASE64(key);

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);//FIXME:这里的初始化值可能是不对的
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        return new String(cipher.doFinal(EncryptUtil.decryptBASE64(data)));
    }

    /**
     * 加密<br>
     * 用公钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, String key)
            throws Exception {
        // 对公钥解密
        byte[] keyBytes = EncryptUtil.decryptBASE64(key);

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);//FIXME:这里的初始化值可能是不对的
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return EncryptUtil.encryptBASE64(cipher.doFinal(data.getBytes("UTF-8")));
    }

    /**
     * 加密<br>
     * 用私钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptByPrivateKey(byte[] data, String key)
            throws Exception {
        // 对密钥解密
        byte[] keyBytes = EncryptUtil.decryptBASE64(key);

        // 取得私钥
        PKCS1EncodedKeySpec pkcs8KeySpec = new PKCS1EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec.getKeySpec());

        // 对数据加密
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);//FIXME:这里的初始化值可能是不对的
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return EncryptUtil.encryptBASE64(cipher.doFinal(data));
    }


    /**
     * 初始化密钥
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);

        KeyPair keyPair = keyPairGen.generateKeyPair();

        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map<String, Object> keyMap = new HashMap<String, Object>(2);

        keyMap.put(Constants.LOCAL_PUBLIC_KEY, publicKey);
        keyMap.put(Constants.LOCAL_PRIVATE_KEY, privateKey);

        return keyMap;
    }

    /**
     * 取得私钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(Constants.LOCAL_PRIVATE_KEY);

        return EncryptUtil.encryptBASE64(key.getEncoded());
    }

    /** */
    /**
     * 取得公钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(Constants.LOCAL_PUBLIC_KEY);

        return EncryptUtil.encryptBASE64(key.getEncoded());
    }

    /**
     * begsession商户订单数据rsamd5
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String signForPKCS1(String data, String privateKey) throws Exception {
        /**
         * 解密由base64编码的私钥
         */
        byte[] keyBytes = EncryptUtil.decryptBASE64(privateKey);

        /**
         * 构造PKCS8EncodedKeySpec对象
         */
        PKCS1EncodedKeySpec pkcs1KeySpec = new PKCS1EncodedKeySpec(keyBytes);

        /**
         * KEY_ALGORITHM 指定的加密算法
         */
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        /**
         * 取私钥匙对象
         */
        PrivateKey priKey = keyFactory.generatePrivate(pkcs1KeySpec.getKeySpec());

        /**
         * 用私钥对信息生成数字签名
         */
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_MD5);
        signature.initSign(priKey);
        signature.update(data.getBytes("UTF-8"));

        return EncryptUtil.encryptBASE64(signature.sign());
    }

}
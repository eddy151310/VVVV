package com.ahdi.lib.utils.security;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtils {

    private SecurityUtils() {
    }

    public static byte[] rsaEncode(byte[] key, byte[] toEncode)
            throws Exception {
        //  客户端需要根据自己的情况去引包
        X509EncodedKeySpec publicKeyX509 = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keyFactory.generatePublic(publicKeyX509));
        return cipher.doFinal(toEncode);
    }

    /**
     * 
     * @param key
     *            aes加密秘钥
     * @param toEncode
     *            待加密数据
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     */
    public static byte[] aesEncode(byte[] key, byte[] toEncode)
            throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec(key));
        return cipher.doFinal(toEncode);
    }

    private static SecretKeySpec getSecretKeySpec(byte[] key) {
        if (key.length < 16) {
            byte[] tmp = new byte[16];
            for (int i = 0; i < key.length; i++) {
                tmp[i] = key[i];
            }
            for (int i = key.length; i < 16; i++) {
                tmp[i] = 0;
            }
            key = tmp;
        }
        return new SecretKeySpec(key, 0, 16, "AES");
    }

    /**
     * 
     * @param key
     *            aes加密秘钥
     * @param toDecode
     *            待解密数据
     * @return
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] aesDecode(byte[] key, byte[] toDecode)
            throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec(key));

        return cipher.doFinal(toDecode);
    }

    public static byte[] md5Sign(byte[] data) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("MD5").digest(data);
    }

}

package com.ahdi.lib.utils.cryptor.aes;

import java.security.Key;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/** 
 * AES Coder<br/> 
 * secret key length:   128bit, default:    128 bit<br/> 
 * mode:    ECB/CBC/PCBC/CTR/CTS/CFB/CFB8 to CFB128/OFB/OBF8 to OFB128<br/> 
 * padding: Nopadding/PKCS5Padding/ISO10126Padding/ 
 * @author chenrunan 
 *  
 */  

public class AESHelper {
    /** 
     * 密钥算法 
    */  
    private static final String KEY_ALGORITHM = "AES";  
      
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";  
      
    private static String mSecretKey = null;
    
    private static String TABLE = new String("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
    
    private static int AES_KEY_LENGTH = 16;
    /** 
     * 初始化密钥 
     *  
     * @return byte[] 密钥  
     * @throws Exception 
     */  
    public static String getSecretKey() {  
    	if(mSecretKey != null)
    		return mSecretKey;
    	
    	byte[] sequence = TABLE.getBytes();
    	Random random = new Random();
    	for(int i = 0; i < sequence.length; i++){
    		int p = random.nextInt(sequence.length);
    		byte tmp = sequence[i];
    		sequence[i] = sequence[p];
    		sequence[p] = tmp;
    	}
    	
    	return mSecretKey = new String(sequence, 0, AES_KEY_LENGTH);
    }  
      
    /** 
     * 转换密钥 
     *  
     * @param key   二进制密钥 
     * @return 密钥 
     */  
    private static Key toKey(byte[] key){  
        //生成密钥  
        return new SecretKeySpec(key, KEY_ALGORITHM);  
    }  
      
    /** 
     * 加密 
     *  
     * @param data  待加密数据 
     * @param key   密钥 
     * @return byte[]   加密数据 
     * @throws Exception 
     */  
    public static byte[] encrypt(byte[] data,Key key) throws Exception{  
        return encrypt(data, key,DEFAULT_CIPHER_ALGORITHM);  
    }  
      
    /** 
     * 加密 
     *  
     * @param data  待加密数据 
     * @param key   二进制密钥 
     * @return byte[]   加密数据 
     * @throws Exception 
     */  
    public static byte[] encrypt(byte[] data,byte[] key) throws Exception{  
        return encrypt(data, key,DEFAULT_CIPHER_ALGORITHM);  
    }  
      
      
    /** 
     * 加密 
     *  
     * @param data  待加密数据 
     * @param key   二进制密钥 
     * @param cipherAlgorithm   加密算法/工作模式/填充方式 
     * @return byte[]   加密数据 
     * @throws Exception 
     */  
    public static byte[] encrypt(byte[] data,byte[] key,String cipherAlgorithm) throws Exception{  
        //还原密钥  
        Key k = toKey(key);  
        return encrypt(data, k, cipherAlgorithm);  
    }  
      
    /** 
     * 加密 
     *  
     * @param data  待加密数据 
     * @param key   密钥 
     * @param cipherAlgorithm   加密算法/工作模式/填充方式 
     * @return byte[]   加密数据 
     * @throws Exception 
     */  
    public static byte[] encrypt(byte[] data,Key key,String cipherAlgorithm) throws Exception{  
        //实例化  
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);  
        //使用密钥初始化，设置为加密模式  
        cipher.init(Cipher.ENCRYPT_MODE, key);  
        //执行操作  
        return cipher.doFinal(data);  
    }  
      
      
      
    /** 
     * 解密 
     *  
     * @param data  待解密数据 
     * @param key   二进制密钥 
     * @return byte[]   解密数据 
     * @throws Exception 
     */  
    public static byte[] decrypt(byte[] data,byte[] key) throws Exception{  
        return decrypt(data, key,DEFAULT_CIPHER_ALGORITHM);  
    }  
      
    /** 
     * 解密 
     *  
     * @param data  待解密数据 
     * @param key   密钥 
     * @return byte[]   解密数据 
     * @throws Exception 
     */  
    public static byte[] decrypt(byte[] data,Key key) throws Exception{  
        return decrypt(data, key,DEFAULT_CIPHER_ALGORITHM);  
    }  
      
    /** 
     * 解密 
     *  
     * @param data  待解密数据 
     * @param key   二进制密钥 
     * @param cipherAlgorithm   加密算法/工作模式/填充方式 
     * @return byte[]   解密数据 
     * @throws Exception 
     */  
    public static byte[] decrypt(byte[] data,byte[] key,String cipherAlgorithm) throws Exception{  
        //还原密钥  
        Key k = toKey(key);
        return decrypt(data, k, cipherAlgorithm);  
    }

    /** 
     * 解密 
     *  
     * @param data  待解密数据 
     * @param key   密钥 
     * @param cipherAlgorithm   加密算法/工作模式/填充方式 
     * @return byte[]   解密数据 
     * @throws Exception 
     */  
    public static byte[] decrypt(byte[] data,Key key,String cipherAlgorithm) throws Exception{  
        //实例化  
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);  
        //使用密钥初始化，设置为解密模式  
        cipher.init(Cipher.DECRYPT_MODE, key);  
        //执行操作  
        return cipher.doFinal(data);  
    }  
}

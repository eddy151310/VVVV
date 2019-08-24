package com.ahdi.wallet.module.QRCode.bean;

/**
 * Date: 2018/5/25 上午9:41
 * Author: kay lau
 * Description:
 */
public class PayCodeInitBean {

    private String randomKey;
    private String clientKey;
    private String publicKey;
    private String privateKey;

    public PayCodeInitBean(String randomKey, String clientKey, String publicKey, String privateKey) {
        this.randomKey = randomKey;
        this.clientKey = clientKey;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getClientKey() {
        return clientKey;
    }

    public String getRandomKey() {
        return randomKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }
}

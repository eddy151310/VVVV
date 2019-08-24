package com.ahdi.wallet.app.bean;

/**
 * Date: 2018/5/17 上午10:05
 * Author: kay lau
 * Description:
 */
public class BindAccountBean {

    /**
     * 银行账户号码。需要加密。加密方式参考附录4.7银行卡类加密方式
     */
    private String bankAccountNo;

    /**
     * 银行账户拥有者的姓名
     */
    private String name;

    /**
     * 银行Code
     */
    private String banckCode;

    /**
     * 银行Code
     */
    private String token;
    /**
     * 手机号码
     */
    private String phone;

    public String getBankAccountNo() {
        return bankAccountNo;
    }

    public void setBankAccountNo(String bankAccountNo) {
        this.bankAccountNo = bankAccountNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBanckCode() {
        return banckCode;
    }

    public void setBanckCode(String banckCode) {
        this.banckCode = banckCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

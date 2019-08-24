package com.ahdi.lib.utils.utils;

/**
 * Date: 2018/12/21 下午6:50
 * Author: kay lau
 * Description:
 */
public class TouchIDStateBean {

    public final String  key_state = "state";
    public final String  key_rsaPrivate = "rsaPrivate";
    public final String  key_payDC = "payDC";
    public final String  key_payPwdVer = "payPwdVer";



    private String lname;
    private boolean state;
    private String rsaPrivate;// 加密后的值
    private String payDC; // 加密后的值
    private String payPwdVer; // 支付密码版本号

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getRsaPrivate() {
        return rsaPrivate;
    }

    public void setRsaPrivate(String rsaPrivate) {
        this.rsaPrivate = rsaPrivate;
    }

    public String getPayDC() {
        return payDC;
    }

    public void setPayDC(String payDC) {
        this.payDC = payDC;
    }

    public String getPayPwdVer() {
        return payPwdVer;
    }

    public void setPayPwdVer(String payPwdVer) {
        this.payPwdVer = payPwdVer;
    }



}

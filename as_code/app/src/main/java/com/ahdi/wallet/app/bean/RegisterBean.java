package com.ahdi.wallet.app.bean;

/**
 * Date: 2018/1/5 下午6:10
 * Author: kay lau
 * Description:
 */
public class RegisterBean {

    private String loginType;
    private String loginName;
    private String loginPassword;
    private String vCode;
    private String token;
    private String agreeVer;

    public String getAgreeVer() {
        return agreeVer;
    }

    public void setAgreeVer(String agreeVer) {
        this.agreeVer = agreeVer;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getvCode() {
        return vCode;
    }

    public void setvCode(String vCode) {
        this.vCode = vCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

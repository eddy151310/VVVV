package com.ahdi.wallet.module.QRCode.bean;

/**
 * Date: 2018/12/4 下午4:09
 * Author: kay lau
 * Description:
 */
public class PayCodePINConfirmBean {

    private String OT;
    private String TT;
    private String payEx;
    private String payAuth;
    private int cancel;

    public String getOT() {
        return OT;
    }

    public void setOT(String OT) {
        this.OT = OT;
    }

    public String getTT() {
        return TT;
    }

    public void setTT(String TT) {
        this.TT = TT;
    }

    public String getPayEx() {
        return payEx;
    }

    public void setPayEx(String payEx) {
        this.payEx = payEx;
    }

    public String getPayAuth() {
        return payAuth;
    }

    public void setPayAuth(String payAuth) {
        this.payAuth = payAuth;
    }

    public int getCancel() {
        return cancel;
    }

    public void setCancel(int cancel) {
        this.cancel = cancel;
    }
}

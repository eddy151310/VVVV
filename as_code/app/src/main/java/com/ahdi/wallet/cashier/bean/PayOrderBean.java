package com.ahdi.wallet.cashier.bean;

import com.ahdi.wallet.cashier.schemas.PayAuthSchema;
import com.ahdi.wallet.app.schemas.TerminalInfoSchema;

import java.util.Map;

/**
 * Date: 2018/1/8 下午4:30
 * Author: kay lau
 * Description:
 */
public class PayOrderBean {

    private String TT;
    private String payEx;
    private String payInfo;
    private PayAuthSchema payAuthSchema;
    private TerminalInfoSchema terminalInfoSchema;
    private Integer payType = -100;
    private String iv;
    private Map<Integer, String> MPay;
    private String rechrAmount;

    public String getRechrAmount() {
        return rechrAmount;
    }

    public void setRechrAmount(String rechrAmount) {
        this.rechrAmount = rechrAmount;
    }

    public Map<Integer, String> getMPay() {
        return MPay;
    }

    public void setMPay(Map<Integer, String> MPay) {
        this.MPay = MPay;
    }


    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
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

    public String getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }

    public PayAuthSchema getPayAuthSchema() {
        return payAuthSchema;
    }

    public void setPayAuthSchema(PayAuthSchema payAuthSchema) {
        this.payAuthSchema = payAuthSchema;
    }

    public TerminalInfoSchema getTerminalInfoSchema() {
        return terminalInfoSchema;
    }

    public void setTerminalInfoSchema(TerminalInfoSchema terminalInfoSchema) {
        this.terminalInfoSchema = terminalInfoSchema;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }
}

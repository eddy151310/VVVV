package com.ahdi.wallet.cashier.bean;

import com.ahdi.wallet.cashier.schemas.PayAuthSchema;
import com.ahdi.wallet.app.schemas.TerminalInfoSchema;

/**
 * Date: 2018/1/8 下午4:59
 * Author: kay lau
 * Description:
 */
public class RechargeOrderBean {
    private String rechr;
    private String payEx;
    private PayAuthSchema payAuthSchema;
    private TerminalInfoSchema terminalInfoSchema;
    private Integer payType;
    private String payInfo;

    public String getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }

    public String getRechr() {
        return rechr;
    }

    public void setRechr(String rechr) {
        this.rechr = rechr;
    }

    public String getPayEx() {
        return payEx;
    }

    public void setPayEx(String payEx) {
        this.payEx = payEx;
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

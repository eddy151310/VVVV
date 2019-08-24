package com.ahdi.wallet.app.bean;

import com.ahdi.wallet.app.schemas.TerminalInfoSchema;

/**
 * Date: 2018/1/8 下午4:46
 * Author: kay lau
 * Description:
 */
public class QueryRechargeListBean {

    private String rechr;
    private TerminalInfoSchema terminalInfoSchema;

    public String getRechr() {
        return rechr;
    }

    public void setRechr(String rechr) {
        this.rechr = rechr;
    }

    public TerminalInfoSchema getTerminalInfoSchema() {
        return terminalInfoSchema;
    }

    public void setTerminalInfoSchema(TerminalInfoSchema terminalInfoSchema) {
        this.terminalInfoSchema = terminalInfoSchema;
    }
}


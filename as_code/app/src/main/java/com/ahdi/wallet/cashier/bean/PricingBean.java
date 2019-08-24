package com.ahdi.wallet.cashier.bean;

import com.ahdi.wallet.app.schemas.LoginSchema;
import com.ahdi.wallet.app.schemas.TerminalInfoSchema;

/**
 * Date: 2018/1/8 下午4:28
 * Author: kay lau
 * Description:
 */
public class PricingBean {

    private String CPOrder;
    private String CfgVersion;
    private TerminalInfoSchema terminalInfoSchema;
    private LoginSchema loginSchema;

    public String getCPOrder() {
        return CPOrder;
    }

    public void setCPOrder(String CPOrder) {
        this.CPOrder = CPOrder;
    }

    public String getCfgVersion() {
        return CfgVersion;
    }

    public void setCfgVersion(String cfgVersion) {
        CfgVersion = cfgVersion;
    }

    public TerminalInfoSchema getTerminalInfoSchema() {
        return terminalInfoSchema;
    }

    public void setTerminalInfoSchema(TerminalInfoSchema terminalInfoSchema) {
        this.terminalInfoSchema = terminalInfoSchema;
    }

    public LoginSchema getLoginSchema() {
        return loginSchema;
    }

    public void setLoginSchema(LoginSchema loginSchema) {
        this.loginSchema = loginSchema;
    }
}

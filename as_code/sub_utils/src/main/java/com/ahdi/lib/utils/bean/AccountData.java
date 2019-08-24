package com.ahdi.lib.utils.bean;

/**
 * Date: 2018/5/30 上午10:30
 * Author: kay lau
 * Description:
 */
public class AccountData {

    /**
     * Account_schema
     */
    private String balance;
    private String status;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public void cleanAccountData(){
        setStatus("");
        setBalance("");
    }
}


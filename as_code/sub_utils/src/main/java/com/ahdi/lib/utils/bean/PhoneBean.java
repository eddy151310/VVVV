package com.ahdi.lib.utils.bean;

/**
 * @author xiaoniu
 * @date 2019/1/30.
 */

public class PhoneBean {

    private String msisdnId;
    private String msisdn;

    public PhoneBean(String msisdnId, String msisdn) {
        this.msisdnId = msisdnId;
        this.msisdn = msisdn;
    }

    public String getMsisdnId() {
        return msisdnId;
    }

    public void setMsisdnId(String msisdnId) {
        this.msisdnId = msisdnId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
}

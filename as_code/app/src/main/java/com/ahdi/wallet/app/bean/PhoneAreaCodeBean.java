package com.ahdi.wallet.app.bean;

/**
 * @author xiaoniu
 * @date 2018/8/17.
 * 电话区号
 */

public class PhoneAreaCodeBean {

    /**国家或者地区的名字*/
    private String areaName;
    /**电话区号*/
    private String phoneAreaCode;

    public PhoneAreaCodeBean(String areaName, String phoneAreaCode) {
        this.areaName = areaName;
        this.phoneAreaCode = phoneAreaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getPhoneAreaCode() {
        return phoneAreaCode;
    }

    public void setPhoneAreaCode(String phoneAreaCode) {
        this.phoneAreaCode = phoneAreaCode;
    }

    @Override
    public String toString() {
        return "name = " + areaName + ", code = " + phoneAreaCode;
    }
}

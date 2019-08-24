package com.ahdi.wallet.app.bean;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/10.
 * @email zhao_zhaohe@163.com
 *
 */

public class IdInfoBean {

    private String key;
    private String value;
    private boolean showRegister;

    public IdInfoBean(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public IdInfoBean(String key, String value, boolean showRegister) {
        this.key = key;
        this.value = value;
        this.showRegister = showRegister;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isShowRegister() {
        return showRegister;
    }

    public void setShowRegister(boolean showRegister) {
        this.showRegister = showRegister;
    }
}

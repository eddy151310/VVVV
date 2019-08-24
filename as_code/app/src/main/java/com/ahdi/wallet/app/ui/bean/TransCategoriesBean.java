package com.ahdi.wallet.app.ui.bean;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/5/25.
 */

public class TransCategoriesBean {
    private int type;
    private String typeString;

    public TransCategoriesBean(int type, String typeString) {
        this.type = type;
        this.typeString = typeString;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeString() {
        return typeString;
    }

    public void setTypeString(String typeString) {
        this.typeString = typeString;
    }
}

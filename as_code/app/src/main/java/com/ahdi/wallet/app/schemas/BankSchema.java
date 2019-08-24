package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

public class BankSchema extends ABSIO {

    /**
     * 银行code
     */
    public String code;
    /**
     * 银行全名
     */
    public String name;

    private String sortLetters;//显示数据拼音的首字母


    @Override
    public void readFrom(JSONObject json) throws JSONException {

        if (json == null) {
            return;
        }
        code = json.optString("Code");
        name = json.optString("Name");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}

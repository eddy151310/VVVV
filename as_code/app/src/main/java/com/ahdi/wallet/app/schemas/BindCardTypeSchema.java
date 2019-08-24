package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

public class BindCardTypeSchema extends ABSIO {

    /**
     * 银行卡类型标识
     */
    public String bank;
    /**
     * 显示银行卡的费率信息
     */
    public String title;

    @Override
    public void readFrom(JSONObject json) throws JSONException {

        if (json == null) {
            return;
        }
        bank = json.optString("BankType");
        title = json.optString("Title");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

    public String getBank() {
        return bank;
    }

    public String getTitle() {
        return title;
    }
}

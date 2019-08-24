package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

public class DescSchema extends ABSIO {

    /**
     * 余额支付方式描述
     */
    public String balance;
    /**
     * 费率描述
     */
    public String fee;

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }

        balance = json.optString("balance");
        fee = json.optString("fee");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }
}

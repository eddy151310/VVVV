package com.ahdi.wallet.cashier.schemas;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

public class PayAuthSchema extends ABSIO {

    private String type;
    private String data;

    public PayAuthSchema(String type, String data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public void readFrom(JSONObject json) throws JSONException {

    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        if (json == null) {
            return null;
        }
        JSONObject schemaJson = new JSONObject();
        if (!TextUtils.isEmpty(type)) {
            schemaJson.put("Type", type);
        }
        if (!TextUtils.isEmpty(data)) {
            schemaJson.put("Data", data);
        }
        json.put("PayAuth", schemaJson);
        return json;
    }
}

package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

public class PaySdkTipsSchema extends ABSIO {

    public String title;
    public String body;

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null){
            return;
        }
        title = json.optString("T");
        body = json.optString("B");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }
}

package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;


public class AuthSchema extends ABSIO {

    public String sid;


    @Override
    public void readFrom(JSONObject json) throws JSONException {

        if (json == null) {
            return;
        }
        sid = json.optString("SID");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }
}

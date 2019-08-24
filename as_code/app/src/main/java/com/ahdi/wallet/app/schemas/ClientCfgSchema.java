package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientCfgSchema extends ABSIO {

    public String cfgversion;
    public String userIcon;
    public String pTypeIcon;
    public String bankIcon;

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        cfgversion = json.optString("cfgversion");
        userIcon = json.optString("userIcon");
        pTypeIcon = json.optString("pTypeIcon");
        bankIcon = json.optString("bankIcon");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

}

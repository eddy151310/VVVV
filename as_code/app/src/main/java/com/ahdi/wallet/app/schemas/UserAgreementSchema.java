package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2018/6/29 下午2:25
 * Author: kay lau
 * Description:
 */
public class UserAgreementSchema extends ABSIO {

    public String name;

    public String url;

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        name = json.optString("Name");
        url = json.optString("Url");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

}

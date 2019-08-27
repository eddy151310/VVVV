package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * @author admin
 */
public class MsgListRsp extends Response {

    public String url;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        url = body.optString("Url");
    }

}

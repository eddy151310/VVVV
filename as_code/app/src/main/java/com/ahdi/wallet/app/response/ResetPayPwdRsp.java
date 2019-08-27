package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/3/15.
 */
public class ResetPayPwdRsp extends Response {

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null){
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
    }

}

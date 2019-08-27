package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * Date: 2018/04/25 10:13
 * Author: kay lau
 * Description:
 */
public class QueryRechrQuotaRsp extends Response {

    public String rechargeAmount;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        rechargeAmount = body.optString("Amount");
    }

}

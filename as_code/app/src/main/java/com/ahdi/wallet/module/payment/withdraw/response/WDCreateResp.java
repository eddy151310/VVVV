package com.ahdi.wallet.module.payment.withdraw.response;


import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/4/9.
 */

public class WDCreateResp extends Response {

    private String ID;
    private String Pay;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        ID = body.optString("ID");
        Pay = body.optString("Pay");
    }

    public String getID() {
        return ID;
    }

    public String getPay() {
        return Pay;
    }
}

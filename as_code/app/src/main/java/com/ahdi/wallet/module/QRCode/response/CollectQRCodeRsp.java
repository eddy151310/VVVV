package com.ahdi.wallet.module.QRCode.response;

import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

public class CollectQRCodeRsp extends Response {

    public String uri;

    public CollectQRCodeRsp() {
        super();
    }

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        uri = body.optString("URI");
    }
}

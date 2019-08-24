package com.ahdi.wallet.module.QRCode.requset;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class CollectQRCodeReq extends Request {

    private String amount;

    public CollectQRCodeReq(String amount, String sid) {
        super(sid);
        this.amount = amount;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;

        JSONObject body = new JSONObject();
        try {
            body.put("Amount", amount);

            json.put(BODY, body);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("CollectQRCodeReq", json.toString());
        return json;
    }

}

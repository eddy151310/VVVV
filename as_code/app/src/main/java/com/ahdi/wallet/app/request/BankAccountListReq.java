package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/3/15.
 */
public class BankAccountListReq extends Request {

    private long timestamp;

    public BankAccountListReq(String sid, long timestamp) {
        super(sid);
        this.timestamp = timestamp;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;

        try {
            JSONObject body = new JSONObject();
            body.put("Timestamp",timestamp);
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("BankAccountListReq", json.toString());
        return json;
    }

}

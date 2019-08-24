package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 查询绑卡详情
 */
public class BindCardDetailsReq extends Request {

    private String bid;


    public BindCardDetailsReq(String sid, String bid) {
        super(sid);
        this.bid = bid;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;

        JSONObject body = new JSONObject();
        try {
            body.put("BID", bid);
            json.put(BODY, body);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("BindCardDetailsReq", json.toString());
        return json;
    }

}

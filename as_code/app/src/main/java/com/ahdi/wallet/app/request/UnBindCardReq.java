package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/3/15.
 */
public class UnBindCardReq extends Request {

    private String BID;
    private String PayPwd;


    public UnBindCardReq(String sid, String BID, String PayPwd) {
        super(sid);
        this.BID = BID;
        this.PayPwd = PayPwd;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;

        JSONObject body = new JSONObject();
        try {
            body.put("BID", BID);
            body.put("PayPwd", PayPwd);
            json.put(BODY, body);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("UnBindCardReq", "UnBindCardReq: " + json.toString());
        return json;
    }

}

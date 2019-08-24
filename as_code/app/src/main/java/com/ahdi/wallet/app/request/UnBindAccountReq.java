package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/3/15.
 */
public class UnBindAccountReq extends Request {

    private String ST;
    private String PayPwd;


    public UnBindAccountReq(String sid, String ST, String PayPwd) {
        super(sid);
        this.ST = ST;
        this.PayPwd = PayPwd;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;

        JSONObject body = new JSONObject();
        try {
            body.put("St", ST);
            body.put("PayPwd", PayPwd);
            json.put(BODY, body);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("UnBindAccountReq", "UnBindAccountReq: " + json.toString());
        return json;
    }

}

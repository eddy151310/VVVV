package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 选择银行卡类型请求
 */
public class SelectCardTypeReq extends Request {

    private String PayPwd;


    public SelectCardTypeReq(String sid, String PayPwd) {
        super(sid);
        this.PayPwd = PayPwd;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;

        JSONObject body = new JSONObject();
        try {
            body.put("PayPwd", PayPwd);
            json.put(BODY, body);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("SelectCardTypeReq", json.toString());
        return json;
    }

}

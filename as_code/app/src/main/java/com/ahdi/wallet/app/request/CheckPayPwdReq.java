package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/3/15.
 */
public class CheckPayPwdReq extends Request {

    private String payPwd;
    private int TType;


    public CheckPayPwdReq(String sid, String payPwd, int TType) {
        super(sid);
        this.payPwd = payPwd;
        this.TType = TType;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("PayPwd", payPwd);
            body.put("TType", TType);
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("CheckPayPwdReq", json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }
}

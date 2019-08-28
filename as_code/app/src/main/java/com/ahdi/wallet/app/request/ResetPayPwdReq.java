package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/3/15.
 */
public class ResetPayPwdReq extends Request {

    private String payPwd;
    private String token;
    private long noPwdLimit;


    public ResetPayPwdReq(String sid, String payPwd, String token, long noPwdLimit) {
        super(sid);
        this.payPwd = payPwd;
        this.token = token;
        this.noPwdLimit = noPwdLimit;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("PayPwd", payPwd);
            body.put("Token", token);
            if (noPwdLimit > 0) {
                body.put("NoPwdLimit", noPwdLimit);
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("resetPayPwdRequest", "resetPayPwdRequest: " + json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }
}

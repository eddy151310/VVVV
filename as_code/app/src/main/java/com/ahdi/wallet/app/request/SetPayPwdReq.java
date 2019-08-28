package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2018/1/4 下午4:46
 * Author: kay lau
 * Description:
 */
public class SetPayPwdReq extends Request {

    private String payPwd;
    private long noPwdLimit;


    public SetPayPwdReq(String sid, String payPwd, long noPwdLimit) {
        super(sid);
        this.payPwd = payPwd;
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
            if (noPwdLimit > 0) {
                body.put("NoPwdLimit", noPwdLimit);
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("SetPayPwdReq", "SetPayPwdReq: " + json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }
}

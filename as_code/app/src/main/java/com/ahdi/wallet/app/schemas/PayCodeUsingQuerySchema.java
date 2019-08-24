package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

public class PayCodeUsingQuerySchema extends ABSIO {

    public boolean isConfirmPayPwd;     // true: 需要确认，采集支付密码
    public String Ex;                   // 支付扩展参数，确认支付时需要回传服务端
    public String Reason;               // 需要确认的原因
    public String Timeslot;             // 用于验证支付授权码MD5值


    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        isConfirmPayPwd = json.optInt("M") == 1; // 0：不需要确认 1：需要确认，采集支付密码
        Ex = json.optString("Ex");
        Reason = json.optString("Reason");
        Timeslot = json.optString("Timeslot");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

}

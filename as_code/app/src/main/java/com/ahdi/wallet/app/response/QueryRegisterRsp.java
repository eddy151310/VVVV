package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * Date: 2017/10/14 下午3:16
 * Author: kay lau
 * Description:
 */
public class QueryRegisterRsp extends Response {

    /**
     * 0 – 未注册
     * 1 – 已注册
     */
    public int SMSSwitch;
    public String msg;
    public String SLName;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        msg = body.optString("Msg");
        SMSSwitch = body.optInt("SMSSwitch");
        SLName = body.optString("SLName");
    }
}

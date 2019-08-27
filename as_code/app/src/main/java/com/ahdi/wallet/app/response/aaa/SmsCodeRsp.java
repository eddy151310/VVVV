package com.ahdi.wallet.app.response.aaa;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * Date: 2018/4/25 下午5:14
 * Author: ibb
 * Description:
 */
public class SmsCodeRsp extends Response {

    public String orderId;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject content = json.optJSONObject(Response.content);
        if (content != null) {
            orderId = content.optString("orderId");
        }
    }

}

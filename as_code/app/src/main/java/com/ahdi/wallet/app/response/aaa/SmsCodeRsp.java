package com.ahdi.wallet.app.response.aaa;

import com.ahdi.wallet.app.schemas.VerifyCodeSchema;
import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * Date: 2018/4/25 下午5:14
 * Author: kay lau
 * Description:
 */
public class SmsCodeRsp extends Response {

    public String reqId;
    public String retCode;
    public String retMsg;
    public String orderId;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        reqId = json.optString("reqId");
        retCode = json.optString("retCode");
        retMsg = json.optString("retMsg");

        orderId = json.optString("orderId");

//        JSONObject content = json.optJSONObject(data);
//        if (content != null) {
//            orderId = json.optString("orderId");
//        }
    }

}

package com.ahdi.wallet.module.payment.topup.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.PaySdkTipsSchema;

import org.json.JSONObject;

public class RechrResultQueryRsp extends Response {

    /**
     * 充值结果(0:等待充值,10:充值中,20:充值失败,30:充值成功)
     */
    public String rechargeResult;

    public String payType;

    public String amount;

    public String serviceFee;

    public String totalAmount;

    private PaySdkTipsSchema tipsSchemas;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        int optInt = body.optInt("Result", -100);
        rechargeResult = String.valueOf(optInt);
        payType = body.optString("PayType");
        amount = body.optString("Amount");
        tipsSchemas = ABSIO.decodeSchema(PaySdkTipsSchema.class, body.optJSONObject("Tips"));
        serviceFee = body.optString("ServiceFee");
        totalAmount = body.optString("TotalAmount");
    }

    public PaySdkTipsSchema getTipsSchemas() {
        return tipsSchemas;
    }
}

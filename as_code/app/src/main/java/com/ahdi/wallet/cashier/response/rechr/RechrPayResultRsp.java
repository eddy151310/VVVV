package com.ahdi.wallet.cashier.response.rechr;

import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

public class RechrPayResultRsp extends Response {

    //    字段名	重要性	类型	描述
    //    Result	必须	String	支付结果(WAIT:等待支付,PAY_ING:支付中,PAY_OK:支付成功,PAY_FAIL:支付失败,LOCAL_PAY_CANCEL:取消支付,PLAT_FAIL:平台处理失败,CLOSE:交易关闭)。
    //    Reason	可选	String	支付失败原因
    //    Err	    可选	String	支付失败原因描述文案

    private String rechargePayResult;
    public String reason;
    public String err;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        rechargePayResult = body.optString("Result");
        reason = body.optString("Reason");
        err = body.optString("Err");
    }

    public String getRechargePayResult() {
        return rechargePayResult;
    }
}

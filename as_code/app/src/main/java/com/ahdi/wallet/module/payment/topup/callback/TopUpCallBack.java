package com.ahdi.wallet.module.payment.topup.callback;

import org.json.JSONObject;

/**
 *
 */
public interface TopUpCallBack {

    /**
     * @param code
     * @param errorMsg
     * @param jsonObject
     * @param OT         充值下单成功之后, 充值支付结果查询时 需要的参数
     */
    void onResult(String code, String errorMsg, JSONObject jsonObject, String OT);
}

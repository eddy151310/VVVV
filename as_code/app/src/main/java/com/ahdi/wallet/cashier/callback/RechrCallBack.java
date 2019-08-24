package com.ahdi.wallet.cashier.callback;

import org.json.JSONObject;

/**
 * Date: 2019/6/4 下午2:02
 * Author: kay lau
 * Description: 用于充值下单
 */
public interface RechrCallBack {

    /**
     * @param code
     * @param errorMsg
     * @param jsonObject
     * @param OT         充值下单成功之后, 充值支付结果查询时 需要的参数
     * @param orderID    充值下单成功之后, 上报需要的参数
     */
    void onResult(String code, String errorMsg, JSONObject jsonObject, String OT, String orderID);
}

package com.ahdi.wallet.cashier.response.pay;

import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

public class PayResultQueryResponse extends Response {

    public String appRespSign;  // 应用支付应答签名，针对支付结果查询，只对支付成功状态有效
    public String payResult;
    public String currency;     // 支付成功时返回支付币种
    public String amt;          // 支付成功时返回支付金额
    public String TransId;      // 交易单号
    public String transCur;     // 交易币种
    public String transAmt;     // 交易金额
    public String exRate;       // 汇率
    public int payType;         // 支付方式
    public String payTypeName;  // 支付方式名称
    public String mer;          // 商户名称
    public String reason;       // 支付失败原因
    public String err;          // 支付失败描述文案
    public String OrderAmt;     // 商品原价金额（基本单位的原始金额）
    public String TipFee;       // 小费
    public String MktAmt;       // 营销抵扣金额，格式化数据
    public String ServiceFee;   // 平台手续费（基本单位的原始金额）

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        JSONObject orderInfo = body.optJSONObject("Pay");
        if (orderInfo != null) {
            appRespSign = orderInfo.optString("AppResp");
            payResult = orderInfo.optString("Result");
            currency = orderInfo.optString("Cur");
            amt = orderInfo.optString("Amt");
            TransId = orderInfo.optString("TransId");
            transCur = orderInfo.optString("TransCur");
            transAmt = orderInfo.optString("TransAmt");
            exRate = orderInfo.optString("ExRate");
            payType = orderInfo.optInt("PayType");
            payTypeName = orderInfo.optString("PayTypeName");
            mer = orderInfo.optString("Mer");
            reason = orderInfo.optString("Reason");
            err = orderInfo.optString("Err");
            OrderAmt = orderInfo.optString("OrderAmt");
            TipFee = orderInfo.optString("TipFee");
            MktAmt = orderInfo.optString("MktAmt");
            ServiceFee = orderInfo.optString("ServiceFee");
        }
    }

}

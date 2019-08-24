package com.ahdi.wallet.cashier.response.rechr;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.PayOrderSchema;
import com.ahdi.wallet.app.schemas.TouchSchema;

import org.json.JSONObject;

public class RechrOrderRsp extends Response {

    private static final String FLAG_Y = "Y";

    public String OT;
    public String orderID;          // 平台生成的支付订单号
    public String PayParam;         // 支付渠道需要的支付参数信息
    public String Invoke;           // 通道调起方式。WEB、SDK、UDS、SMS、NO等
    public String Channel;          // 通道标识
    public int ChannelID;           // 通道ID编号
    public boolean isHidden;        // 第三方支付渠道是否隐式调起
    private PayOrderSchema payOrder;
    private TouchSchema touchSchema;

    public RechrOrderRsp() {
        super();
    }

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        OT = body.optString("OT");
        payOrder = ABSIO.decodeSchema(PayOrderSchema.class, body.optJSONObject("PayOrder"));
        if (payOrder != null) {
            orderID = payOrder.ID;
            PayParam = payOrder.PayParam;
            Invoke = payOrder.Invoke;
            Channel = payOrder.Channel;
            ChannelID = payOrder.ChannelID;
            isHidden = FLAG_Y.equals(payOrder.HiddenFlag);
        }
        touchSchema = ABSIO.decodeSchema(TouchSchema.class, body.optJSONObject("Touch"));
    }

    public TouchSchema getTouchSchema() {
        return touchSchema;
    }

    public PayOrderSchema getPayOrder() {
        return payOrder;
    }
}

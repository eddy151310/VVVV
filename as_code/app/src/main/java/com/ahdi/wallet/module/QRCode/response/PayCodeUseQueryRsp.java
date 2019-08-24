package com.ahdi.wallet.module.QRCode.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.PayCodeUsingQuerySchema;
import com.ahdi.wallet.app.schemas.PayOrderSchema;
import com.ahdi.wallet.cashier.schemas.TransSchema;

import org.json.JSONObject;

public class PayCodeUseQueryRsp extends Response {

    public String tt;
    public String ot;
    public boolean isConfirmPayPwd;     // true: 需要确认，采集支付密码
    public String Ex;                   // 支付扩展参数，确认支付时需要回传服务端
    public String Reason;               // 需要确认的原因

    private static final String FLAG_Y = "Y";
    public String ID;                   // 平台生成的支付订单号
    public String PayParam;             // 支付渠道需要的支付参数信息
    public String Invoke;               // 通道调起方式。WEB、SDK、UDS、SMS、NO等
    public String Channel;              // 通道标识
    public int ChannelID;               // 通道ID编号
    public boolean isHidden;            // 第三方支付渠道是否隐式调起
    private PayOrderSchema payOrder;
    public int touchFlag;
    public String transID;


    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }

        TransSchema transSchema = ABSIO.decodeSchema(TransSchema.class, body.optJSONObject("Trans"));
        if (transSchema != null) {
            tt = transSchema.TT;
            transID = transSchema.ID;
        }

        ot = body.optString("OT");
        PayCodeUsingQuerySchema payCodeUsingQuerySchema = ABSIO.decodeSchema(PayCodeUsingQuerySchema.class, body.optJSONObject("Pay"));
        if (payCodeUsingQuerySchema != null) {
            isConfirmPayPwd = payCodeUsingQuerySchema.isConfirmPayPwd;
            Ex = payCodeUsingQuerySchema.Ex;
            Reason = payCodeUsingQuerySchema.Reason;

        }
        payOrder = ABSIO.decodeSchema(PayOrderSchema.class, body.optJSONObject("PayOrder"));
        if (payOrder != null) {
            ID = payOrder.ID;
            PayParam = payOrder.PayParam;
            Invoke = payOrder.Invoke;
            Channel = payOrder.Channel;
            ChannelID = payOrder.ChannelID;
            isHidden = FLAG_Y.equals(payOrder.HiddenFlag);
        }
        touchFlag = body.optInt("TouchFlag", -100);
    }

    public PayOrderSchema getPayOrder() {
        return payOrder;
    }

}

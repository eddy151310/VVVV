package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

public class PayOrderSchema extends ABSIO {

    public String ID;            // 平台生成的支付订单号
    public String PayParam;      // 支付渠道需要的支付参数信息
    public String Invoke;        // 通道调起方式。WEB、SDK、UDS、SMS、NO等
    public String Channel;       // 通道标识
    public int ChannelID;        // 通道ID编号
    public String HiddenFlag;    // 第三方支付渠道是否隐式调起

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        ID = json.optString("ID");
        PayParam = json.optString("PayParam");
        Invoke = json.optString("Invoke");
        Channel = json.optString("Channel");
        ChannelID = json.optInt("ChannelID", 0);
        HiddenFlag = json.optString("HiddenFlag");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

}

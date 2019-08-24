package com.ahdi.wallet.cashier.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

public class MarketPayTypeSchema extends ABSIO {

    public int Id;              // 营销方式id
    public String Name;         // 营销方式名称
    public String Key;          // 营销方式key
    public String PayEx;        // 营销支付数据
    public String Money;        // 券扣减金额（基本单位的原始金额）
    public int Num;             // 已选张数
    public String Block;        // 出现该数据时（该字段值不为null），支付方式不可用。该值内容为不可用原因描述。
    public String BReason;      // 不可用原因，常用值有：BALANCE_NOT_ENOUGH（余额不足）
    public String TotalMoney;   // 付款总金额（基本单位的原始金额）用券抵扣金额>=支付金额时，返回该属性。


    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        Id = json.optInt("Id");
        Name = json.optString("Name");
        Key = json.optString("Key");
        PayEx = json.optString("PayEx");
        Money = json.optString("Money");
        Num = json.optInt("Num");
        Block = json.optString("Block");
        BReason = json.optString("BReason");
        TotalMoney = json.optString("TotalMoney");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

}

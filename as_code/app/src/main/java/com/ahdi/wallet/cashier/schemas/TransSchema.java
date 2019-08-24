package com.ahdi.wallet.cashier.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

public class TransSchema extends ABSIO {

    public String ID;           // 交易流水号
    public String GName;        // 商品名称
    public String subject;      // 子商品名称，用于展示手续费等
    public int Flags;           // 是否只支持余额支付:0否，1是
    public String Mer;          // 收款商户
    public String Price;        // 商品价格, 提现时为提现总金额
    public String TT;           // 交易订单token
    public String AppId;        // 应用appid（用于数据采集）
    public String TransPrice;   // 交易金额（格式化金额数据），提现时表示提现金额(到账金额)
    public String Fee;          // 提现时，表示提现服务费（格式化金额数据）
    public String BizType;      // 订单业务类型编号 102000001-支付交易, 200000001-转账支付, 200100001-提现


    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        ID = json.optString("ID");
        GName = json.optString("GName");
        subject = json.optString("Subject");
        Flags = json.optInt("Flags", -1);
        Mer = json.optString("Mer");
        Price = json.optString("Price");
        TT = json.optString("TT");
        AppId = json.optString("AppId");
        TransPrice = json.optString("TransPrice");
        Fee = json.optString("Fee");
        BizType = json.optString("BizType");

    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }
}

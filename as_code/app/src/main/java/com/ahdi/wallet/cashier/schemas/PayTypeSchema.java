package com.ahdi.wallet.cashier.schemas;

import android.text.TextUtils;

import com.ahdi.wallet.app.schemas.DescSchema;
import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.lib.utils.utils.AppGlobalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PayTypeSchema extends ABSIO {

    public Integer ID;              // 支付方式ID
    public String IconUrl;          // 支付方式图标标识
    public String Name;             // 支付方式名称
    public String RechrRate;        // 充值手续费率表达式
    public String PayLimit;         // 支付限额表达式。这个充值和支付情况下可能不同。作为充值列表时存在
    public String PayEx;            // 支付下单时，需要同步上传的数据
    private DescSchema descSchema;  // 支付方式 (描述信息, 立减信息)
    public String Block;            // 出现该数据时（该字段值不为null），支付方式不可用。该值内容为不可用原因。
    public String TotalMoney;       // 交易总金额=交易金额+通道手续费（格式化后的金额数据）。
    public String Fee;              // 通道手续费，（格式化金额数据）。条件：通道无手续费时，不返回该属性
    public JSONArray collects;      // 支付需要采集的数据。

    @Override
    public void readFrom(JSONObject json) throws JSONException {

        if (json == null) {
            return;
        }
        ID = json.optInt("ID");
        IconUrl = json.optString("Icon");
        Name = json.optString("Name");
        RechrRate = json.optString("RechrRate");
        PayLimit = json.optString("PayLimit");
        PayEx = json.optString("PayEx");
        Block = json.optString("Block");
        descSchema = ABSIO.decodeSchema(DescSchema.class, json.optJSONObject("Desc"));
        collects = json.optJSONArray("Collect");
        TotalMoney = json.optString("TotalMoney");
        Fee = json.optString("Fee");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

    public DescSchema getDescSchema() {
        return descSchema;
    }

    /**
     * 选择该支付方式需要采集的信息
     *
     * @return
     */
    public String getPayInfo() {
        if (collects == null || collects.length() < 1) {
            return "";
        }
        JSONObject object = new JSONObject();
        for (int i = 0; i < collects.length(); i++) {
            try {
                String collect = collects.getString(i);
                if (TextUtils.equals(collect, "deviceId")) {
                    object.put("deviceId", AppGlobalUtil.getInstance().getBCADeviceID());
                } else if (TextUtils.equals(collect, "userAgent")) {
                    object.put("userAgent", AppGlobalUtil.getInstance().getBCAUserAgent());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return object.toString();
    }
}

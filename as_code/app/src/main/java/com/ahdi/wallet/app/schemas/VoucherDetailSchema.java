package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 优惠券
 */
public class VoucherDetailSchema extends ABSIO {

    //    字段名	重要性	属性	描述
    //    Coupon	必须	Coupon_Schema	券信息
    //    OthLimits	必须	String[]	券使用其它限制：
    //    pay_limit-支付方式限制;
    //    mer_limit-商户限制;
    //    trade_limit-商户行业限制；
    //    use_time-可用时段
    //    un_use_time-不可用时段
    //    share_rule-优惠活动共享规则
    //    refund_rule-退款规则
    //    Info	条件	String	优惠券使用说明描述

    private VoucherSchema voucherSchema;
    private List<String> othLimits = null;
    private String info;

    public VoucherDetailSchema() {
    }

    public VoucherDetailSchema(VoucherSchema voucherSchema, List<String> othLimits, String info) {
        this.voucherSchema = voucherSchema;
        this.othLimits = othLimits;
        this.info = info;
    }

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        voucherSchema = ABSIO.decodeSchema(VoucherSchema.class, json.optJSONObject("Coupon"));
        jsonToList(json.optJSONArray("OthLimits"));
        info = json.optString("Info");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

    private void jsonToList(JSONArray jsonArray){
        if (jsonArray == null){
            return;
        }
        othLimits = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++){
            othLimits.add(jsonArray.optString(i));
        }
    }

    public VoucherSchema getVoucherSchema() {
        return voucherSchema;
    }

    public List<String> getOthLimits() {
        return othLimits;
    }

    public String getInfo() {
        return info;
    }
}

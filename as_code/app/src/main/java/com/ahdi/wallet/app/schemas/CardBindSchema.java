package com.ahdi.wallet.app.schemas;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

public class CardBindSchema extends ABSIO {

    /**
     * 绑卡ID
     */
    public String bid;
    /**
     * 银行名称
     */
    public String bank;
    /**
     * 银行标识
     */
    public String icon;
    /**
     * 银行卡类型：储蓄卡、信用卡
     */
    public String type;
    /**
     * 卡号后4位
     */
    public String card;
    /**
     * 点击银行卡时的跳转地址。为可选值，该值没有时，无点击事件
     */
    public String link;


    public boolean isEditLimit;     // 是否可以调整限额

    public String limitDesc;        // 卡日支付限额展示文案
    public String feeDesc;          // 卡单笔交易费率展示文案
    public String bankType;         // 银行类型标识银行类型标识 BCA-表示BCA卡  MayBank-表示MayBank卡


    @Override
    public void readFrom(JSONObject json) throws JSONException {

        if (json == null) {
            return;
        }
        bid = json.optString("BID");
        bank = json.optString("Bank");
        icon = json.optString("Icon");
        type = json.optString("Type");
        card = json.optString("Card");
        link = json.optString("Link");
        isEditLimit = json.optInt("IsEditLimit") == 0 ? false : true;
        limitDesc = json.optString("LimitDesc");
        feeDesc = json.optString("FeeDesc");
        bankType = json.optString("BankType");
        if (TextUtils.isEmpty(icon)) {
            icon = "1.png";
        }
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

}

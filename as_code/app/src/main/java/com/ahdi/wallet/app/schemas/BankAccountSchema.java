package com.ahdi.wallet.app.schemas;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

public class BankAccountSchema extends ABSIO {

    /**
     * 银行账户绑定id生成的token
     */
    public String st;
    /**
     * 银行
     */
    public String bank;
    /**
     * 银行标识
     */
    public String icon;
    /**
     * 银行账户号码前两位+后四位
     */
    public String accountNo;


    @Override
    public void readFrom(JSONObject json) throws JSONException {

        if (json == null) {
            return;
        }
        st = json.optString("St");
        bank = json.optString("Bank");
        icon = json.optString("Icon");
        accountNo = json.optString("AccountNo");
        if (TextUtils.isEmpty(icon)) {
            icon = "1.png";
        }
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

    public String getSt() {
        return st;
    }

    public String getBank() {
        return bank;
    }

    public String getIcon() {
        return icon;
    }

    public String getAccountNo() {
        return accountNo;
    }
}

package com.ahdi.wallet.module.payment.withdraw.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/4/9.
 */

public class WDBankAccountSchema extends ABSIO {

    public String Bank;
    public String Icon;
    public String Account;
    public String Token;

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        Bank = json.optString("Bank");
        Icon = json.optString("Icon");
        Account = json.optString("Account");
        Token = json.optString("Token");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

    public String getBank() {
        return Bank;
    }

    public String getIcon() {
        return Icon;
    }

    public String getAccount() {
        return Account;
    }

    public String getToken() {
        return Token;
    }
}

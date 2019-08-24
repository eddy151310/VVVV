package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * Date: 2017/10/14 下午3:16
 * Author: kay lau
 * Description:
 */
public class BindAccountRsp extends Response {

    private String BankName;
    private String BankAccount;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        BankName = body.optString("BankName");
        BankAccount = body.optString("BankAccount");
    }

    public String getBankName() {
        return BankName;
    }

    public String getBankAccount() {
        return BankAccount;
    }
}

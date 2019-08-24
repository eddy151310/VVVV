package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountSchema extends ABSIO {

    /**
     * UNSAFE: 非安全（未设置支付密码）;
     * SAFE:安全（设置支付密码）
     * LOCK:锁定（支付密码输错超过次数等）
     */
    public String status;

    public String balance;

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        status = json.optString("Status");
        balance = json.optString("Balance");

    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

}

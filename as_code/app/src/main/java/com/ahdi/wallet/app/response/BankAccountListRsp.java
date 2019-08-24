package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.BankSchema;

import org.json.JSONObject;

public class BankAccountListRsp extends Response {

    private BankSchema[] bank;
    private long timestamp;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        bank = ABSIO.decodeSchemaArray(BankSchema.class, "Bank", body);
        timestamp = body.optLong("Timestamp");
    }

    public BankSchema[] getBank() {
        return bank;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

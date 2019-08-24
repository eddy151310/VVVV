package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.BankAccountSchema;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/3/15.
 */
public class QueryBankAccountInfoRsp extends Response {

    private BankAccountSchema[] bind;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        bind = ABSIO.decodeSchemaArray(BankAccountSchema.class, "BankAcc", body);
    }

    public BankAccountSchema[] getBind() {
        return bind;
    }
}

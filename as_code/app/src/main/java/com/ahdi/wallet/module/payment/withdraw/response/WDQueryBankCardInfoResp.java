package com.ahdi.wallet.module.payment.withdraw.response;

import com.ahdi.wallet.module.payment.withdraw.schemas.WDBankAccountSchema;
import com.ahdi.wallet.module.payment.withdraw.schemas.WDLimitSchema;
import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/4/9.
 */

public class WDQueryBankCardInfoResp extends Response {

    private String balance;
    private String FeeExp;
    private WDBankAccountSchema[] BAccounts;
    private long min;
    private long max;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        BAccounts = ABSIO.decodeSchemaArray(WDBankAccountSchema.class, "BAccounts", body);
        FeeExp = body.optString("FeeExp");
        balance = body.optString("Balance");
        WDLimitSchema limit = ABSIO.decodeSchema(WDLimitSchema.class, body.optJSONObject("Limit"));
        if (limit != null) {
            min = limit.min;
            max = limit.max;
        }
    }

    public String getBalance() {
        return balance;
    }

    public WDBankAccountSchema[] getBAccounts() {
        return BAccounts;
    }

    public String getFeeExp() {
        return FeeExp;
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }
}

package com.ahdi.wallet.module.payment.withdraw.schemas;


import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/4/9.
 */

public class WDLimitSchema extends ABSIO {

    public long min;
    public long max;

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        min = json.optLong("min");
        max = json.optLong("max");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }
}

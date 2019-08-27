package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.UserBillSchema;

import org.json.JSONObject;

public class TransListRsp extends Response {

    private UserBillSchema[] bills;
    private int more;
    private long time;
    private int rang;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        bills = ABSIO.decodeSchemaArray(UserBillSchema.class, "Bills", body);
        more = body.optInt("More");
        time = body.optLong("Time");
        rang = body.optInt("Rang");
    }

    public UserBillSchema[] getBills() {
        return bills;
    }

    public int getMore() {
        return more;
    }

    public long getTime() {
        return time;
    }

    public int getRang() {
        return rang;
    }
}

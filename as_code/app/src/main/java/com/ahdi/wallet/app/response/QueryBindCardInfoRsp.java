package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.CardBindSchema;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/3/15.
 */
public class QueryBindCardInfoRsp extends Response {

    private CardBindSchema[] bind;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        bind = ABSIO.decodeSchemaArray(CardBindSchema.class, "Bind", body);
    }

    public CardBindSchema[] getBind() {
        return bind;
    }
}

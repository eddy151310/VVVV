package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.CardBindSchema;

import org.json.JSONObject;

/**
 * 查询绑卡详情响应
 */
public class BindCardDetailsRsp extends Response {

    private CardBindSchema bindSchema;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        bindSchema = ABSIO.decodeSchema(CardBindSchema.class,body.optJSONObject("Bind"));
    }

    public CardBindSchema getBindSchema() {
        return bindSchema;
    }
}

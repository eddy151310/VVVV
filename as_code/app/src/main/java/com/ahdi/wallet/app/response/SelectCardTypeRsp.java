package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.BindCardTypeSchema;

import org.json.JSONObject;

/**
 * 选择银行卡类型响应
 */
public class SelectCardTypeRsp extends Response {

    private BindCardTypeSchema[] cardTypeSchema;
    private String token;//支付密码验证通过token

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        cardTypeSchema = ABSIO.decodeSchemaArray(BindCardTypeSchema.class,"CardTypes",body);
        token = body.optString("Token");
    }

    public BindCardTypeSchema[] getCardTypeSchema() {
        return cardTypeSchema;
    }

    public String getToken() {
        return token;
    }
}

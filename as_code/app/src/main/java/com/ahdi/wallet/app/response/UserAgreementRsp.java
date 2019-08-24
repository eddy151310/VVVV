package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.UserAgreementSchema;

import org.json.JSONObject;

/**
 * Date: 2018/6/29 下午2:25
 * Author: kay lau
 * Description:
 */
public class UserAgreementRsp extends Response {

    private UserAgreementSchema[] agreementSchemas;
    public String version;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        agreementSchemas = ABSIO.decodeSchemaArray(UserAgreementSchema.class, "Agreements", body);
        version = body.optString("Version");
    }

    public UserAgreementSchema[] getAgreementSchemas() {
        return agreementSchemas;
    }
}

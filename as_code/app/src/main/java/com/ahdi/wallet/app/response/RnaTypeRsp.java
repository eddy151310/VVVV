package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.RnaTypeSchema;

import org.json.JSONObject;

/**
 * 实名认证类型结果响应
 *
 * @author zhaohe
 */
public class RnaTypeRsp extends Response {

//    字段名	重要性	类型	描述
//    Types	    必选	RNATypeSchema[]实名认证类型

    private RnaTypeSchema[] rnaTypeSchemas;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        rnaTypeSchemas = ABSIO.decodeSchemaArray(RnaTypeSchema.class, "Types", body);
    }

    public RnaTypeSchema[] getRnaTypeSchemas() {
        return rnaTypeSchemas;
    }

    public String[] getTypesSchemas() {

        if (rnaTypeSchemas != null && rnaTypeSchemas.length > 1) {
            String[] types = new String[rnaTypeSchemas.length];
            for (int i = 0; i < rnaTypeSchemas.length; i++) {
                types[i] = rnaTypeSchemas[i].getAbbr();
            }
            return types;
        }
        return null;
    }


}

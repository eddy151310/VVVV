package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.TouchSchema;

import org.json.JSONObject;

/**
 * Date: 2019/3/22 下午3:20
 * Author: kay lau
 * Description:
 */
public class OpenTouchIDPayRsp extends Response {

    private TouchSchema touchschema;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        touchschema = ABSIO.decodeSchema(TouchSchema.class, body.optJSONObject("Touch"));
    }

    public TouchSchema getTouchSchema() {
        return touchschema;
    }
}

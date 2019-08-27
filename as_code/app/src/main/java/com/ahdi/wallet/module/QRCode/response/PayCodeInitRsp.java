package com.ahdi.wallet.module.QRCode.response;


import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.module.QRCode.schemas.AuthCodePaySchema;

import org.json.JSONObject;

public class PayCodeInitRsp extends Response {

    public String serverData;
    public long timeStamp;
    public AuthCodePaySchema[] authCodePaySchemas;

    public PayCodeInitRsp() {
        super();
    }

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        serverData = body.optString("ServerData");
        timeStamp = body.optLong("TimeStamp");
        authCodePaySchemas = ABSIO.decodeSchemaArray(AuthCodePaySchema.class, "PayList", body);
    }
}

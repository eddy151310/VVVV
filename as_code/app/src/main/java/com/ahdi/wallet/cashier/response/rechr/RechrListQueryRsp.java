package com.ahdi.wallet.cashier.response.rechr;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.cashier.schemas.PayTypeSchema;

import org.json.JSONObject;

public class RechrListQueryRsp extends Response {

    private PayTypeSchema[] payTypeSchemas;
    public String amount;
    public String subject;
    public int TouchFlag;

    public RechrListQueryRsp() {
        super();
    }

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        subject = body.optString("Subject");
        amount = body.optString("Amount");
        payTypeSchemas = ABSIO.decodeSchemaArray(PayTypeSchema.class, "RechrTypes", body);
        TouchFlag = body.optInt("TouchFlag", -100);
    }

    public PayTypeSchema[] getPayTypeSchemas() {
        return payTypeSchemas;
    }

}

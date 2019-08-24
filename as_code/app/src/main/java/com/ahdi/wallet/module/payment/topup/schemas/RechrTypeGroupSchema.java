package com.ahdi.wallet.module.payment.topup.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

public class RechrTypeGroupSchema extends ABSIO {

    public int Gid;                                 // 分组id标识
    public String Name;                             // 分组名称
    public String Desc;                             // 分组描述
    private RechrTypeSchema[] rechrTypeSchemas;     // 充值类型信息

    public RechrTypeSchema[] getRechrTypeSchemas() {
        return rechrTypeSchemas;
    }

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        Gid = json.optInt("Gid");
        Name = json.optString("Name");
        Desc = json.optString("Desc");
        rechrTypeSchemas = ABSIO.decodeSchemaArray(RechrTypeSchema.class, "RechrTypes", json);
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }
}

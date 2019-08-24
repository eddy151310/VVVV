package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.ExtSchema;
import com.ahdi.wallet.app.schemas.ScanCheckSchema;

import org.json.JSONObject;

/**
 * 扫码结果check
 */
public class ScanCheckRsp extends Response {

    private ScanCheckSchema schema = null;
    /**扩展数据*/
    private ExtSchema extSchema = null;
    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null){
            return;
        }

        JSONObject dataJson = json.optJSONObject(data);
        if (dataJson == null) {
            return;
        }

        schema = ABSIO.decodeSchema(ScanCheckSchema.class,dataJson);
        extSchema = ABSIO.decodeSchema(ExtSchema.class,dataJson.optJSONObject("Ext"));
    }

    public ScanCheckSchema getSchema() {
        return schema;
    }

    public ExtSchema getExtSchema() {
        return extSchema;
    }
}

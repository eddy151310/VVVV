package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.PhoneAreaCodeSchema;

import org.json.JSONObject;

/**
 * 区号
 *
 * @author zhaohe
 */
public class PhoneAreaCodeRsp extends Response {

    //    字段名	重要性	类型	描述
    //    Countries	必填	Country_Schema
    //    国家区域编码列表，按照首字母排序
    //    Version	可选	Long	国家区域编码版本号

    public long version;
    public PhoneAreaCodeSchema[] phoneAreaCodeSchemas;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        version = body.optLong("Version");
        phoneAreaCodeSchemas = ABSIO.decodeSchemaArray(PhoneAreaCodeSchema.class, "Countries", body);
    }

}

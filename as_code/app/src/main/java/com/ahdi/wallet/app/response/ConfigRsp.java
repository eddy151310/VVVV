package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.ConfigSchema;

import org.json.JSONObject;

/**
 * 配置信息结果响应
 *
 * @author zhaohe
 */
public class ConfigRsp extends Response {

    //    字段名	    重要性	类型	描述
    //    ClientCfg	    可选	Map 	配置参数信息，为空表示参数不需要更新。参考4.1 ClientCfg

    private ConfigSchema appCfgSchema = null;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        appCfgSchema = ABSIO.decodeSchema(ConfigSchema.class, body.optJSONObject("ClientCfg"));
    }

    public ConfigSchema getAppCfgSchema() {
        return appCfgSchema;
    }
}

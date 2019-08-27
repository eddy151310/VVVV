package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.PaySdkTipsSchema;

import org.json.JSONObject;

/**
 * 实名认证确认结果响应
 *
 * @author zhaohe
 */
public class RNASubmitAllRsp extends Response {

    //    字段名	重要性	类型	描述
//    Prompt	必须	Map<String, String>	提示信息 T：提示title  B：提示内容
    private PaySdkTipsSchema tipsSchemas;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        tipsSchemas = ABSIO.decodeSchema(PaySdkTipsSchema.class, body.optJSONObject("Prompt"));
    }

    public PaySdkTipsSchema getTipsSchemas() {
        return tipsSchemas;
    }
}

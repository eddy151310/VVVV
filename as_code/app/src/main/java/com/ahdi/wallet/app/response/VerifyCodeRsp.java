package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.VerifyCodeSchema;

import org.json.JSONObject;

/**
 * Date: 2018/4/25 下午5:14
 * Author: kay lau
 * Description:
 */
public class VerifyCodeRsp extends Response {

    //    字段名	重要性	类型	描述
    //    Vcode	    必须	vcode_schema 验证短信验证码的结果
    //    Token	    必须	String	身份证明，在2.4.1.2传入，证明已验证过短信

    private VerifyCodeSchema verifyCodeSchema;
    public String token;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        token = body.optString("Token");
        verifyCodeSchema = ABSIO.decodeSchema(VerifyCodeSchema.class, body.optJSONObject("Vcode"));
    }

    public VerifyCodeSchema getVerifyCodeSchema() {
        return verifyCodeSchema;
    }

}

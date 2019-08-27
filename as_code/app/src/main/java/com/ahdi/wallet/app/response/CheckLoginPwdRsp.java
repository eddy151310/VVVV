package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * 检测登录密码的响应
 *
 * @author zhaohe
 */
public class CheckLoginPwdRsp extends Response {

    //    字段名	重要性	类型	描述
    //    Token	必须	String	登录密码验证成功token

    private String token;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        token = body.optString("Token");
    }

    public String getToken() {
        return token;
    }
}

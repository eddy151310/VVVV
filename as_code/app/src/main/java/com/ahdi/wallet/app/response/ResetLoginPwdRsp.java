package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.AccountSchema;
import com.ahdi.wallet.app.schemas.AuthSchema;
import com.ahdi.wallet.app.schemas.UserSchema;

import org.json.JSONObject;

/**
 * 重置或者找回 登录密码的响应
 *
 * @author zhaohe
 */
public class ResetLoginPwdRsp extends Response {

    //    字段名	重要性	类型	描述
    //    User	必须	User schema 登录用户信息
    //    Auth	必须	Auth schema 登录用户session

    private UserSchema userSchema = null;
    private AuthSchema authSchema = null;
    private AccountSchema accountSchema = null;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        userSchema = ABSIO.decodeSchema(UserSchema.class, body.optJSONObject("User"));
        authSchema = ABSIO.decodeSchema(AuthSchema.class, body.optJSONObject("Auth"));
        accountSchema = ABSIO.decodeSchema(AccountSchema.class, body.optJSONObject("Account"));
    }

    public UserSchema getUserSchema() {
        return userSchema;
    }

    public AuthSchema getAuthSchema() {
        return authSchema;
    }

    public AccountSchema getAccountSchema() {
        return accountSchema;
    }
}

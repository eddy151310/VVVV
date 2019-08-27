package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.AccountSchema;
import com.ahdi.wallet.app.schemas.AuthSchema;
import com.ahdi.wallet.app.schemas.UserSchema;

import org.json.JSONObject;

/**
 * Date: 2017/10/14 下午3:16
 * Author: kay lau
 * Description:
 */
public class RegisterRsp extends Response {

    private AuthSchema Auth;
    private UserSchema User;
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
        Auth = ABSIO.decodeSchema(AuthSchema.class, body.optJSONObject("Auth"));
        User = ABSIO.decodeSchema(UserSchema.class, body.optJSONObject("User"));
        accountSchema = ABSIO.decodeSchema(AccountSchema.class, body.optJSONObject("Account"));
    }

    public AuthSchema getAuth() {
        return Auth;
    }

    public UserSchema getUser() {
        return User;
    }

    public AccountSchema getAccountSchema() {
        return accountSchema;
    }
}

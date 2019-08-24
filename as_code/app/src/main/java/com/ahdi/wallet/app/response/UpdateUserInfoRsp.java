package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.UserSchema;

import org.json.JSONObject;

/**
 * 更新用户信息的接口响应
 *
 * @author zhaohe
 */
public class UpdateUserInfoRsp extends Response {

    private UserSchema userSchema = null;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        userSchema = ABSIO.decodeSchema(UserSchema.class, body.optJSONObject("User"));
    }

    public UserSchema getUserSchema() {
        return userSchema;
    }
}

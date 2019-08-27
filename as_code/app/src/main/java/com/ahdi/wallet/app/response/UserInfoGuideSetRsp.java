package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.UserSchema;

import org.json.JSONObject;

/**
 * Date: 2018/7/2 下午3:47
 * Author: kay lau
 * Description:
 */
public class UserInfoGuideSetRsp extends Response {

    private UserSchema User;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }

        User = ABSIO.decodeSchema(UserSchema.class, body.optJSONObject("User"));
    }

    public UserSchema getUser() {
        return User;
    }

}

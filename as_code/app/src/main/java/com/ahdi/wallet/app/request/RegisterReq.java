package com.ahdi.wallet.app.request;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.wallet.app.schemas.RegisterSchema;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2018/1/4 下午4:46
 * Author: kay lau
 * Description:
 */
public class RegisterReq extends Request {

    private RegisterSchema registerSchema;
    private String token;

    public RegisterReq(RegisterSchema registerSchema, String token) {
        this.registerSchema = registerSchema;
        this.token = token;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            if (registerSchema != null) {
                registerSchema.writeTo(body);
            }
            if (!TextUtils.isEmpty(token)) {
                body.put("Token", token);
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("RegisterReq", "Register set lp Request: " + json.toString());
        return json;
    }
}

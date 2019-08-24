package com.ahdi.wallet.app.request;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhaohe
 * 设置登录密码（重置和找回密码）
 */
public class ResetLoginPwdReq extends Request {

    private static final String TAG = ResetLoginPwdReq.class.getSimpleName();

    private String pwd = "";
    private String token = "";

    public ResetLoginPwdReq(String sid, String pwd, String token) {
        super(sid);
        this.pwd = pwd;
        this.token = token;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            if (!TextUtils.isEmpty(pwd)) {
                body.put("Pwd", pwd);
            }
            if (!TextUtils.isEmpty(token)) {
                body.put("Token", token);
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, TAG + json.toString());
        return json;
    }

}

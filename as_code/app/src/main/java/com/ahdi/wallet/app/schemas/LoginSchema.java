package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.lib.utils.cryptor.aes.AesKeyCryptor;
import com.ahdi.lib.utils.config.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2017/10/16 下午2:45
 * Author: kay lau
 * Description:
 */
public class LoginSchema extends ABSIO {

    private String loginType;
    private String loginName;
    private String loginPassword;

    public LoginSchema(String loginType, String loginName, String loginPassword) {
        this.loginType = loginType;
        this.loginName = loginName;
        this.loginPassword = loginPassword;
    }

    @Override
    public void readFrom(JSONObject json) throws JSONException {

    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        if (json == null) {
            return null;
        }
        JSONObject schemaJson = new JSONObject();
        schemaJson.put("Type", loginType);
        schemaJson.put("LName", loginName);

        if (loginType.equals(Constants.LP_KEY)) {
            // LP - 登录名+密码   (密码需要加密)
            schemaJson.put("Pwd", AesKeyCryptor.encodeLoginPwd(loginPassword));

        } else if (loginType.equals(Constants.LV_KEY)
                || loginType.equals(Constants.LS_KEY)) {
            //  LS LV  凭证不需要加密
            schemaJson.put("Pwd", loginPassword);
        }

        json.put("Login", schemaJson);
        return json;
    }
}

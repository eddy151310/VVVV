package com.ahdi.wallet.app.response.aaa;

import com.ahdi.wallet.network.framwork.Response;
import org.json.JSONObject;

/**
 * Author: ibb
 * Description:
 */
public class LoginSMSRsp extends Response {

    public String userId;
    public String sid;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }

        JSONObject content = json.optJSONObject(Response.content);
        if (content != null) {
            userId = content.optString("userId");
            sid = content.optString("sid");
        }
    }

}


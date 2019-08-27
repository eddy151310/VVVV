package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * Date: 2017/10/16 下午3:15
 * Author: kay lau
 * Description:
 */
public class TerminalBind_LogoutRsp extends Response {

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
    }
}

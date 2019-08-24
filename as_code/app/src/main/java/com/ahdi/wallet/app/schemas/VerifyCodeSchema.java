package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyCodeSchema extends ABSIO {

    public Integer Wait;   // 允许再次下发验证码等待时长

    public Integer Remain; // 剩余允许验证次数

    public String Desc;    // 短信提示文案

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        Wait = json.optInt("Wait");
        Remain = json.optInt("Remain");
        Desc = json.optString("Desc");

    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

}

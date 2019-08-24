package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * 是否可以绑卡响应
 */
public class IsCanBindRsp extends Response {

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null){
            return;
        }
    }

}

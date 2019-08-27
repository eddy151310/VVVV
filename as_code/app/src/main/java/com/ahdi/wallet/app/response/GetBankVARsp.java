package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * 实名认证审核结果响应
 *
 * @author zhaohe
 */
public class GetBankVARsp extends Response {

//    字段名	重要性	类型	描述
//    URL	    必填	String	bank va url充值引导页面跳转地址

    public String url;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        url = body.optString("URL");

    }
}

package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2018/1/4 下午5:56
 * Author: kay lau
 * Description:
 */
public class QueryBankAccountReq extends Request {

    public QueryBankAccountReq(String sid) {
        super(sid);
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null){
            return null;
        }
        try {
            JSONObject body = new JSONObject();
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("QueryBankAccountReq", "QueryBankAccountReq: " + json.toString());
        return json;
    }
}

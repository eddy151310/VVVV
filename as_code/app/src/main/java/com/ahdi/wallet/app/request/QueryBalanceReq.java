package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

/**
 * Date: 2018/1/4 下午5:56
 * Author: kay lau
 * Description:
 */
public class QueryBalanceReq extends Request {

    public QueryBalanceReq(String sid) {
        super(sid);
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json != null) {
            LogUtil.e("QueryBalanceReq", "QueryBalanceReq: " + json.toString());
        }
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }
}

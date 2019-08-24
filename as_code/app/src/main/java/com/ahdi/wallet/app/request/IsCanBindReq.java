package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 是否可以绑卡
 */
public class IsCanBindReq extends Request {

    private static final String TAG = IsCanBindReq.class.getSimpleName();

    public IsCanBindReq(String sid) {
        super(sid);
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, json.toString());
        return json;
    }

}

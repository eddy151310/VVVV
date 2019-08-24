package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhaohe
 * 实名认证种类
 */
public class RnaTypeReq extends Request {

    private static final String TAG = RnaTypeReq.class.getSimpleName();

    public RnaTypeReq(String sid) {
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
        LogUtil.e(TAG, TAG + json.toString());
        return json;
    }

}

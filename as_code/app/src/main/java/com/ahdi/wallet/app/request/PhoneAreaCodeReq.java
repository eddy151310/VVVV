package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhaohe
 */
public class PhoneAreaCodeReq extends Request {

    private static final String TAG = PhoneAreaCodeReq.class.getSimpleName();

    private long version;

    public PhoneAreaCodeReq(long version) {
        this.version = version;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            if (version > 0) {
                body.put("Version", version);
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, json.toString());
        return json;
    }
    @Override
    protected JSONObject getContentJson() {
        return null;
    }
}

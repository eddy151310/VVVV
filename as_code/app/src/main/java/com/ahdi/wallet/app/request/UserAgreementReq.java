package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2018/6/29 下午2:25
 * Author: kay lau
 * Description:
 */
public class UserAgreementReq extends Request {

    private static final String TAG = UserAgreementReq.class.getSimpleName();

    private String action;

    public UserAgreementReq(String action) {
        this.action = action;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("Action", action);
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, TAG + ": " + json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }

}

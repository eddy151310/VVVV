package com.ahdi.wallet.app.request.aaa;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.Request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author: ibb
 * Description:
 */
public class UserCenterReq extends Request {

    private static final String TAG = UserCenterReq.class.getSimpleName();

    private String sid;
    private String moblie;

    public UserCenterReq(String sid , String moblie) {
        this.sid = sid;
        this.moblie = moblie;
    }


    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
            return json;
    }

    @Override
    protected JSONObject getContentJson() {
        JSONObject content = new JSONObject();
        try {

            content.put("reqId", TAG);//通讯流水

            if (!TextUtils.isEmpty(sid)) {
                content.put("sid", sid);
            }

            if (!TextUtils.isEmpty(moblie)) {
                content.put("mobile", moblie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return content;
    }
}

package com.ahdi.wallet.app.request.aaa;

import android.text.TextUtils;

import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.network.framwork.Request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author: ibb
 * Description:
 */
public class UserInfoReq extends Request {

    private static final String TAG = UserInfoReq.class.getSimpleName();

    private String sid;

    public UserInfoReq(String sid) {
        this.sid = sid;
    }


    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;

        JSONObject body = new JSONObject();

        JSONObject content = new JSONObject();
        try {

            content.put("reqId", TAG);//通讯流水

            if (!TextUtils.isEmpty(sid)) {
                content.put("mobile", sid);
            }

            body.put(CONTENT , content); //body包含的第一部分 content
            body.put(CONTENT_SIGN, md5WithContent(content.toString()));//body包含的第二部分 contentSign

            json.put(BODY ,body);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG, TAG + ": " + json.toString());
        return json;
    }
}

package com.ahdi.wallet.app.request;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhaohe
 * 修改用户信息
 */
public class UpdateUserInfoReq extends Request {

    private static final String TAG = UpdateUserInfoReq.class.getSimpleName();

    private int type = 0;
    private String content = "";

    public UpdateUserInfoReq(String sid, String content, int type) {
        super(sid);
        this.type = type;
        this.content = content;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            if (type > 0) {
                body.put("Type", type);
            }
            if (!TextUtils.isEmpty(content)) {
                body.put("Content", content);
            }
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

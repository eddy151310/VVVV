package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.wallet.app.schemas.AvatarSchema;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhaohe
 * 修改用户头像
 */
public class SetUserIconReq extends Request {

    private static final String TAG = SetUserIconReq.class.getSimpleName();

    private AvatarSchema avatarSchema = null;

    public SetUserIconReq(String sid, AvatarSchema avatarSchema) {
        super(sid);
        this.avatarSchema = avatarSchema;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            if (avatarSchema != null) {
                avatarSchema.writeTo(body);
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, TAG + json.toString());
        return json;
    }
}

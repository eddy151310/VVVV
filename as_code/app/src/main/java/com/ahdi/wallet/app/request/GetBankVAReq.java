package com.ahdi.wallet.app.request;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhaohe
 */
public class GetBankVAReq extends Request {

    private static final String TAG = GetBankVAReq.class.getSimpleName();

    private String type;

    public GetBankVAReq(String type, String sid) {
        super(sid);
        this.type = type;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            if (!TextUtils.isEmpty(type)) {
                body.put("Type", type);
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, json.toString());
        return json;
    }
}

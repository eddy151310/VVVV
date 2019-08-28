package com.ahdi.wallet.module.payment.withdraw.requset;

import android.text.TextUtils;

import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.network.framwork.Request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/4/9.
 */

public class WDQueryProgressRequest extends Request {

    private static final String TAG = WDQueryProgressRequest.class.getSimpleName();

    private String ID;

    public WDQueryProgressRequest(String ID, String sid) {
        super(sid);
        this.ID = ID;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            if (!TextUtils.isEmpty(ID)) {
                body.put("ID", ID);
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

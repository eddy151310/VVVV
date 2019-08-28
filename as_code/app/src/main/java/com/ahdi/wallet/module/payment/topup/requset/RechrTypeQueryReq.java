package com.ahdi.wallet.module.payment.topup.requset;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2018/04/25
 * Author: kay lau
 * Description:
 */
public class RechrTypeQueryReq extends Request {

    private static final String TAG = RechrTypeQueryReq.class.getSimpleName();

    public RechrTypeQueryReq(String sid) {
        super(sid);
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }
        try {
            JSONObject body = new JSONObject();
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

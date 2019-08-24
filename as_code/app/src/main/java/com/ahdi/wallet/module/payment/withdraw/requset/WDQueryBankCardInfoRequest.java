package com.ahdi.wallet.module.payment.withdraw.requset;

import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.network.framwork.Request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/4/9.
 */

public class WDQueryBankCardInfoRequest extends Request {

    private static final String TAG = WDQueryBankCardInfoRequest.class.getSimpleName();

    public WDQueryBankCardInfoRequest(String sid) {
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
        LogUtil.e(TAG, json.toString());
        return json;
    }
}

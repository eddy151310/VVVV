package com.ahdi.wallet.module.payment.topup.requset;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2018/1/4 下午4:46
 * Author: kay lau
 * Description:
 */
public class RechrResultQueryReq extends Request {

    private static final String TAG = RechrResultQueryReq.class.getSimpleName();

    private String OT;

    public RechrResultQueryReq(String OT, String sid) {
        super(sid);
        this.OT = OT;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("OT", OT);
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, "充值结果查询: " + json.toString());
        return json;
    }
}

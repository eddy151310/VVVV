package com.ahdi.wallet.cashier.requset.rechr;

import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.network.framwork.Request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2018/1/4 下午4:46
 * Author: kay lau
 * Description:
 */
public class RechrPayResultQueryReq extends Request {

    private static final String TAG = RechrPayResultQueryReq.class.getSimpleName();

    private String OT;

    public RechrPayResultQueryReq(String OT, String sid) {
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
        LogUtil.e(TAG, "充值支付结果查询: " + json.toString());
        return json;
    }
    @Override
    protected JSONObject getContentJson() {
        return null;
    }
}

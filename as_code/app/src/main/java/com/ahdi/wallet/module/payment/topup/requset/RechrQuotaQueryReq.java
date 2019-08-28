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
public class RechrQuotaQueryReq extends Request {

    private static final String TAG = RechrQuotaQueryReq.class.getSimpleName();

    public RechrQuotaQueryReq(String sid) {
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
        LogUtil.e(TAG, "查询充值额度: " + json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }
}

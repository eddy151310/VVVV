package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhaohe
 * 优惠券详情
 */
public class VoucherDetailReq extends Request {

    private static final String TAG = VoucherDetailReq.class.getSimpleName();

    private String id;

    public VoucherDetailReq(String sid, String id) {
        super(sid);
        this.id = id;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("Id", id);
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, json.toString());
        return json;
    }

}

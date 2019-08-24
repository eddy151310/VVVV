package com.ahdi.wallet.app.request;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhaohe
 * 优惠券列表
 */
public class VoucherListReq extends Request {

    private static final String TAG = VoucherListReq.class.getSimpleName();

    private String id;
    private int cType;

    public VoucherListReq(String sid, String id, int cType) {
        super(sid);
        this.id = id;
        this.cType = cType;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            if (!TextUtils.isEmpty(id)) {
                body.put("Id", id);
            }
            body.put("CType", cType);
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, json.toString());
        return json;
    }

}

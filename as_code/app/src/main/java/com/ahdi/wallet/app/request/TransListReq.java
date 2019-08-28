package com.ahdi.wallet.app.request;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class TransListReq extends Request {

    private String last;
    private int type;
    private long month;

    public TransListReq(String sid, String last, int type, long month) {
        super(sid);
        this.last = last;
        this.type = type;
        this.month = month;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;

        JSONObject body = new JSONObject();
        try {
            if (!TextUtils.isEmpty(last)) {
                body.put("Last", last);
            }
            if (type != -1) {
                body.put("Type", type);
            }
            if (month != -1) {
                body.put("Month", month);
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("TransListReq", "TransListReq: " + json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }

}

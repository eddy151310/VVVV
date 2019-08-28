package com.ahdi.wallet.cashier.requset.pay;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2018/1/4 下午4:46
 * Author: kay lau
 * Description:
 */
public class PayResultQueryRequest extends Request {

    private String OT;
    private String TT;

    public PayResultQueryRequest(String OT, String TT, String sid) {
        super(sid);
        this.OT = OT;
        this.TT = TT;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            if (!TextUtils.isEmpty(OT)) {
                body.put("OT", OT);
            }
            body.put("TT", TT);
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("PayResultQueryRequest", json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }
}

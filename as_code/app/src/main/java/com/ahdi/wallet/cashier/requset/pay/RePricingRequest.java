package com.ahdi.wallet.cashier.requset.pay;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class RePricingRequest extends Request {

    private String TT;
    private String touchVer;

    public RePricingRequest(String TT, String touchVer, String sid) {
        super(sid);
        this.TT = TT;
        this.touchVer = touchVer;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;

        JSONObject body = new JSONObject();
        try {
            body.put("TT", TT);
            if (!TextUtils.isEmpty(touchVer)) {
                body.put("TouchVer", touchVer);
            }
            json.put(BODY, body);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("RePricingRequest", "RePricingRequest:" + json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }

}

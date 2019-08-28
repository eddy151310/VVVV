package com.ahdi.wallet.bca.requset;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author admin
 * @date 2019/01/14
 */

public class BcaOTPReq extends Request {

    private static final String TAG = BcaOTPReq.class.getSimpleName();

    private String msisdnId;
    private String payEx;
    private String payInfo;

    public BcaOTPReq(String sid,String msisdnId, String payEx, String payInfo) {
        super(sid);
        this.msisdnId = msisdnId;
        this.payEx = payEx;
        this.payInfo = payInfo;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }
        JSONObject body = new JSONObject();
        try {
            body.put("MsisdnId", msisdnId);
            body.put("PayEx", payEx);
            if (!TextUtils.isEmpty(payInfo)){
                body.put("PayInfo", payInfo);
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, "BcaOTPReq: " + json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }
}

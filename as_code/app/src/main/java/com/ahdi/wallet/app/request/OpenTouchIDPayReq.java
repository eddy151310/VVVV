package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2019/3/22 下午3:00
 * Author: kay lau
 * Description:
 */
public class OpenTouchIDPayReq extends Request {

    private static final String TAG = OpenTouchIDPayReq.class.getSimpleName();

    private String payPwdCipher;
    private String publicKey;
    private String sign;

    public OpenTouchIDPayReq(String payPwdCipher, String publicKey, String sign, String sid) {
        super(sid);
        this.payPwdCipher = payPwdCipher;
        this.publicKey = publicKey;
        this.sign = sign;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }
        JSONObject body = new JSONObject();
        try {
            body.put("PayPasswd", payPwdCipher);
            body.put("PubKey", publicKey);
            body.put("Sign", sign);
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }
}

package com.ahdi.wallet.app.request.aaa;

import android.text.TextUtils;

import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.network.framwork.Request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author: ibb
 * Description:
 */
public class SmsCodeReq extends Request {

    private static final String TAG = SmsCodeReq.class.getSimpleName();

    private String phoneNumber;

    /**
     * 下发短信验证码---专用
     *
     * @param phoneNumber
     */
    public SmsCodeReq(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
            return json;
    }

    @Override
    protected JSONObject getContentJson() {

        JSONObject content = new JSONObject();
        try {

            content.put("reqId", TAG);//通讯流水

            if (!TextUtils.isEmpty(phoneNumber)) {
                content.put("mobile", phoneNumber);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return content;
    }
}

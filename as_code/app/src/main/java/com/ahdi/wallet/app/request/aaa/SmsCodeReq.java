package com.ahdi.wallet.app.request.aaa;

import android.text.TextUtils;

import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.network.framwork.Request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2018/4/25 下午5:10
 * Author: kay lau
 * Description:
 */
public class SmsCodeReq extends Request {

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
        if (json == null)
            return null;

        JSONObject body = new JSONObject();

        JSONObject content = new JSONObject();
        try {

            content.put("reqId", "123");//通讯流水

            if (!TextUtils.isEmpty(phoneNumber)) {
                content.put("mobile", phoneNumber);
            }

            body.put(CONTENT , content); //body包含的第一部分 content
            body.put(CONTENT_SIGN, md5WithContent(content.toString()));//body包含的第二部分 contentSign

            json.put(BODY ,body);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.d("SmsCodeReq", "SmsCodeReq: " + json.toString());
        return json;
    }
}

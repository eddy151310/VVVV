package com.ahdi.wallet.app.request.aaa;

import android.text.TextUtils;

import com.ahdi.lib.utils.cryptor.EncryptUtil;
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

        JSONObject content = new JSONObject();
        try {
            if (!TextUtils.isEmpty(phoneNumber)) {
                content.put("mobile", phoneNumber);
            }
            json.put(CONTENT, content);

            String contentStr = content.toString();

            String md5 = "6d34707a3433306b98693";
            String signInfo = EncryptUtil.md5(contentStr + "&" + md5);
            json.put(SIGN_INFO, signInfo);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.d("SmsCodeReq", "SmsCodeReq: " + json.toString());
        return json;
    }
}

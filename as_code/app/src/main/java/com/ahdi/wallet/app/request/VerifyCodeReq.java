package com.ahdi.wallet.app.request;

import android.text.TextUtils;

import com.ahdi.lib.utils.cryptor.EncryptUtil;
import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2018/4/25 下午5:10
 * Author: kay lau
 * Description:
 */
public class VerifyCodeReq extends Request {

    private String phoneNumber;
    private String verifyCode;


    /**
     * 下发短信验证码---专用
     *
     * @param phoneNumber
     */
    public VerifyCodeReq(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * 验证: 短信验证码---专用
     *
     * @param phoneNumber
     */
    public VerifyCodeReq(String phoneNumber, String verifyCode, String sid) {
        super(sid);
        this.phoneNumber = phoneNumber;
        this.verifyCode = verifyCode;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;

        JSONObject content = new JSONObject();
        try {
            if (!TextUtils.isEmpty(phoneNumber)) {
                content.put("Tel", phoneNumber);
            }
            if (!TextUtils.isEmpty(verifyCode)) {
                content.put("VerifyCode", verifyCode);
            }
            json.put(CONTENT, content);

            String contentStr = content.toString();

            String md5 = "6d34707a3433306b98693";
            String signInfo = EncryptUtil.md5(contentStr + "&" + md5);
            json.put(CONTENT_SIGN, signInfo);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("SmsCodeReq", "SmsCodeReq: " + json.toString());
        return json;
    }
}

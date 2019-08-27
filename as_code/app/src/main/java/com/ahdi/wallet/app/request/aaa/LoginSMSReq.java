package com.ahdi.wallet.app.request.aaa;

import android.text.TextUtils;

import com.ahdi.lib.utils.cryptor.EncryptUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.network.framwork.Request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2019/8/26 下午5:10
 * Author: ibb
 * Description:
 */
public class LoginSMSReq extends Request {

    private String mobile;
    private String smsCode;
    private String orderId;


    /**
     *
     * @param mobile
     */
    public LoginSMSReq(String mobile, String smsCode, String smsOrderid) {
        this.mobile = mobile;
        this.smsCode = smsCode;
        this.orderId = smsOrderid;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;

        JSONObject body = new JSONObject();

        JSONObject content = new JSONObject();
        try {

            content.put("reqId", "123");//通讯流水

            if (!TextUtils.isEmpty(mobile)) {
                content.put("mobile", mobile);
            }
            if (!TextUtils.isEmpty(smsCode)) {
                content.put("smsCode", smsCode);
            }
            if (!TextUtils.isEmpty(orderId)) {
                content.put("orderId", orderId);
            }

            body.put(CONTENT , content); //body包含的第一部分 content
            body.put(CONTENT_SIGN, md5WithContent(content.toString()));//body包含的第二部分 contentSign

            json.put(BODY ,body);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.d("LoginSMSReq", "LoginSMSReq: " + json.toString());
        return json;
    }
}

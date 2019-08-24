package com.ahdi.wallet.app.schemas;

import android.text.TextUtils;

import com.ahdi.wallet.app.bean.RegisterBean;
import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2017/10/16 下午2:45
 * Author: kay lau
 * Description:
 */
public class RegisterSchema extends ABSIO {

    /**
     * loginType     注册账号类型（默认为Cm）：Cm:普通注册，Ph:手机号注册，Em：邮箱注册
     * loginName     登录名
     * loginPassword 登录密码。需要加密。加密方式参考附录4.6密码类加密方式
     * vCode         注册用验证码
     * agreeVer      用户已同意的注册协议版本
     */
    private String loginType;
    private String loginName;
    private String loginPassword;
    private String vCode;
    private String agreeVer;

    public RegisterSchema(RegisterBean bean) {
        this.loginType = bean.getLoginType();
        this.loginName = bean.getLoginName();
        this.loginPassword = bean.getLoginPassword();
        this.vCode = bean.getvCode();
        this.agreeVer = bean.getAgreeVer();
    }

    @Override
    public void readFrom(JSONObject json) throws JSONException {

    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        if (json == null) {
            return null;
        }
        JSONObject schemaJson = new JSONObject();
        if (!TextUtils.isEmpty(loginType)) {
            schemaJson.put("Type", loginType);
        }
        schemaJson.put("LName", loginName);
        schemaJson.put("AgreeVer", agreeVer);
        schemaJson.put("Pwd", loginPassword);
        if (!TextUtils.isEmpty(vCode)) {
            schemaJson.put("VCode", vCode);
        }
        json.put("Reg", schemaJson);
        return json;
    }
}

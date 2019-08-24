package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

public class UserSchema extends ABSIO {

    public String LName;   // 登录名
    public String NName;   // 用户昵称
    public int uid;        // 账号标识
    /**
     * 免密码登录凭证，如果是登录名+密码登录，则本字段必有
     */
    public String Voucher;
    public String Avatar;   // 用户头像 url
    public boolean isAuth;  // 实名认证状态：false 未认证，true 已认证
    public int gender;
    public String birthday;
    public String email;
    public String sLName;   // 带有*的登录名，用于展示
    public String AreaCode; // 国家区域编码

    @Override
    public void readFrom(JSONObject json) throws JSONException {

        if (json == null) {
            return;
        }
        LName = json.optString("LName");
        NName = json.optString("NName");
        uid = json.optInt("UID");
        Avatar = json.optString("Avatar");
        Voucher = json.optString("Voucher");
        int rna = json.optInt("RNA");
        isAuth = rna == 1; // 实名认证状态：0未认证，1已认证
        gender = json.optInt("Gender");
        birthday = json.optString("Birthday");
        email = json.optString("Email");
        sLName = json.optString("SLName");
        AreaCode = json.optString("AreaCode");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }
}

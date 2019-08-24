package com.ahdi.wallet.app.request;

import android.text.TextUtils;

import com.ahdi.wallet.app.bean.UserInfoGuideSetBean;
import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2018/7/2 下午3:38
 * Author: kay lau
 * Description:
 */
public class UserInfoGuideSetReq extends Request {

    private String nname;
    private String bithdate;
    private int gender;
    private String email;

    public UserInfoGuideSetReq(String sid, UserInfoGuideSetBean bean) {
        super(sid);
        if (bean != null) {
            this.nname = bean.getNname();
            this.bithdate = bean.getBithdate();
            this.gender = bean.getGender();
            this.email = bean.getEmail();
        }
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;

        JSONObject body = new JSONObject();
        try {
            if (!TextUtils.isEmpty(nname)) {
                body.put("NName", nname);
            }

            body.put("Gender", gender);

            if (!TextUtils.isEmpty(bithdate)) {
                body.put("Bithdate", bithdate);
            }
            if (!TextUtils.isEmpty(email)) {
                body.put("Email", email);
            }
            json.put(BODY, body);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("UserInfoGuideSet", json.toString());
        return json;
    }
}

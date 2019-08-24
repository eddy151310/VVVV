package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2018/1/4 下午4:46
 * Author: kay lau
 * Description:
 */
public class QueryRegisterReq extends Request {

    private static final String TAG = QueryRegisterReq.class.getSimpleName();

    //    LName	必须	String	登录名
    //    Type	条件	int	业务类型，
    //    0-账号是否已注册，
    //    1-账号是否存在

    private String loginName;
    private int type;

    public QueryRegisterReq(String loginName, int type) {
        this.loginName = loginName;
        this.type = type;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("LName", loginName);
            body.put("Type", type);
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, TAG + ": " + json.toString());
        return json;
    }
}

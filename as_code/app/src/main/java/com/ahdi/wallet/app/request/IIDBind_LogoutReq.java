package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

/**
 * Date: 2018/1/4 下午5:56
 * Author: kay lau
 * Description:
 */
public class IIDBind_LogoutReq extends Request {

    private static final String TAG = IIDBind_LogoutReq.class.getSimpleName();

    public IIDBind_LogoutReq(String sid) {
        super(sid);
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        return json;
    }
}

package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

/**
 * Date: 2019/3/22 下午3:05
 * Author: kay lau
 * Description:
 */
public class CloseTouchIDPayReq extends Request {

    public CloseTouchIDPayReq(String sid) {
        super(sid);
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;
        LogUtil.e("CloseTouchIDPayReq", json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }
}

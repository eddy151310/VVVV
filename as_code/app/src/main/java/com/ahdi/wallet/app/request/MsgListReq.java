package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

public class MsgListReq extends Request {

    private static final String TAG = MsgListReq.class.getSimpleName();

    public MsgListReq(String sid) {
        super(sid);
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }
        LogUtil.e(TAG, "消息列表请求:" + json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }

}

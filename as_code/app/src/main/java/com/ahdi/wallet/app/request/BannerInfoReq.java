package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

/**
 * @author zhaohe
 * banner info
 */
public class BannerInfoReq extends Request {

    private static final String TAG = BannerInfoReq.class.getSimpleName();

    private String requestParam;

    public BannerInfoReq(String requestParam) {
        this.requestParam = requestParam;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }
        LogUtil.e(TAG, json.toString());
        return json;
    }

    @Override
    public String execute() {
        LogUtil.e(TAG, "requestParam = " + requestParam);
        return requestParam;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }
}

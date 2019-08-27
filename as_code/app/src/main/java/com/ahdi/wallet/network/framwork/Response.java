package com.ahdi.wallet.network.framwork;

import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

public abstract class Response {

    private static final String TAG = Response.class.getSimpleName();

    public static final String content = "content";
    protected ABSHeader mHeader = new ABSHeader();

    public abstract void bodyReadFrom(JSONObject json);

    public static <T extends Response> T decodeJson(Class<T> clazz, JSONObject json) {
        if (null != json) {
            try {
                T t = clazz.newInstance();
                t.readFrom(json);
                return t;
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "error:" + e.toString());
            }
        }

        return null;
    }

    public void readFrom(JSONObject json) {
        try {
            mHeader.readFrom(json);
            bodyReadFrom(json);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "error:" + e.toString());
        }
    }

    public ABSHeader getmHeader() {
        return mHeader;
    }
}

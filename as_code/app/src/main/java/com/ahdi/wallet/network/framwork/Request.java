package com.ahdi.wallet.network.framwork;

import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Request {

    private static final String TAG = Request.class.getSimpleName();

    protected ABSHeader mHeader;
    protected String BODY = "content";
    protected String CONTENT = "content";
    protected String SIGN_INFO = "signInfo";

    public Request() {
        mHeader = new ABSHeader();
    }

    public Request(String sid) {
        mHeader = new ABSHeader(sid);
    }

    public Request(String sid, int tzOffset) {
        mHeader = new ABSHeader(sid, tzOffset);
    }

    protected abstract JSONObject bodyWriteTo(JSONObject json);

    public String execute() {
        try {
            JSONObject jsons = bodyWriteTo(new JSONObject());
            return jsons.toString();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "error:" + e.toString());

        } catch (Error e) {
            e.printStackTrace();
            LogUtil.e(TAG, "error:" + e.toString());
        }

        return null;
    }

    public ABSHeader getmHeader() {
        return mHeader;
    }

}

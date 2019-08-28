package com.ahdi.wallet.network.framwork;

import com.ahdi.lib.utils.cryptor.EncryptUtil;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Request {

    private static final String TAG = Request.class.getSimpleName();

    protected ABSHeader mHeader;
    protected String BODY = "body";
    protected String CONTENT = "content";
    protected String CONTENT_SIGN = "contentSign";

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
    protected abstract JSONObject getContentJson();

    public String execute() {
        try {

            JSONObject jsonRequest = new JSONObject();


            JSONObject content  = getContentJson();

            JSONObject body = new JSONObject();
            body.put(CONTENT , content); //body包含的第一部分 content
            body.put(CONTENT_SIGN, md5WithContent(content.toString()));//body包含的第二部分 contentSign

            jsonRequest.put(BODY ,body );               // 请求内容---body
            jsonRequest =  mHeader.writeTo(jsonRequest);// 请求内容---header
            LogUtil.d("" , "请求参数:" + jsonRequest.toString());
            return jsonRequest.toString();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "error:" + e.toString());

        } catch (Error e) {
            e.printStackTrace();
            LogUtil.e(TAG, "error:" + e.toString());
        }

        return null;
    }


    public String md5WithContent(String content) {
        final String md5 = "6d34707a3433306b98693";
        return EncryptUtil.md5(content + "&" + md5);
    }

    public ABSHeader getmHeader() {
        return mHeader;
    }

}

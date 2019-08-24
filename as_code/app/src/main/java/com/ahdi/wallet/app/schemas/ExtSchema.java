package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 扫码扩展数据
 */
public class ExtSchema extends ABSIO {

    //    字段名	重要性	类型	描述
    //    Method	必须	String	请求类型：POST
    //    Body	    必须	String	请求参数，打开http地址时提交（webview），Content-Type默认为：application/x-www-form-urlencoded

    public String method = "";
    public String body = "";
    public String query = "";

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        method = json.optString("Method");
        body = json.optString("Body");
        query = json.optString("Query");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

}

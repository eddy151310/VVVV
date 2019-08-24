package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhaohe
 */
public class RnaTypeSchema extends ABSIO {

    //    字段名	重要性	属性	描述
    //    Abbr	    必须	String	简称
    //    FullName	必须	String	全称

    //    实名认证类型：
    //    KTP，印尼身份证；
    //    Passport，护照；
    //    KITAS，印尼临时居留证

    /**
     * 简称
     */
    private String abbr;
    /**
     * 全称
     */
    private String fullName;


    @Override
    public void readFrom(JSONObject json) throws JSONException {

        if (json == null) {
            return;
        }
        abbr = json.optString("Abbr");
        fullName = json.optString("FullName");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return json;
    }

    public String getAbbr() {
        return abbr;
    }

    public String getFullName() {
        return fullName;
    }
}

package com.ahdi.wallet.app.schemas;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.lib.utils.config.ConfigCountry;

import org.json.JSONException;
import org.json.JSONObject;

public class PhoneAreaCodeSchema extends ABSIO {

    //    字段名	重要性	类型	描述
    //    Code	必须	String	国家区域编码
    //    Name	必须	String	国家名称

    public String code;
    public String name;

    public String firstLetter;
    public boolean isShowFirstLetter;

    /**添加+之后的code*/
    public String formatCode;

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        code = json.optString("Code");
        name = json.optString("Name");
        if (!TextUtils.isEmpty(name)){
            firstLetter = name.substring(0, 1).toUpperCase();
            if (!firstLetter.matches("[A-Z]")) {
                firstLetter = "#";
            }
        }
        formatCode = ConfigCountry.KEY_ADD.concat(code);
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }
}

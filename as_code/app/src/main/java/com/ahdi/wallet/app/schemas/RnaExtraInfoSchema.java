package com.ahdi.wallet.app.schemas;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhaohe
 * 实名认证补充信息
 */
public class RnaExtraInfoSchema extends ABSIO {

    //    字段名	    重要性	类型	描述
    //    Nationality	必须	String	国籍
    //    Birthplace	必须	String	出生地
    //    Occupation	必须	String	职业
    //    MotherName	必须	String	母亲的姓名

    private String nationality;
    private String birthplace;
    private String occupation;
    private String motherName;

    public RnaExtraInfoSchema(String nationality, String birthplace, String occupation, String motherName) {
        this.nationality = nationality;
        this.birthplace = birthplace;
        this.occupation = occupation;
        this.motherName = motherName;
    }

    @Override
    public void readFrom(JSONObject json) throws JSONException {

    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        if (json == null) {
            return null;
        }
        JSONObject rnaExtraInfo = new JSONObject();
        if (!TextUtils.isEmpty(nationality)) {
            rnaExtraInfo.put("Nationality", nationality);
        }
        if (!TextUtils.isEmpty(birthplace)) {
            rnaExtraInfo.put("Birthplace", birthplace);
        }
        if (!TextUtils.isEmpty(occupation)) {
            rnaExtraInfo.put("Occupation", occupation);
        }
        if (!TextUtils.isEmpty(motherName)) {
            rnaExtraInfo.put("MotherName", motherName);
        }
        json.put("ExtraInfo", rnaExtraInfo);
        return json;
    }

}

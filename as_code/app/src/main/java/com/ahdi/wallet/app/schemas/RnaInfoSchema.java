package com.ahdi.wallet.app.schemas;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhaohe
 */
public class RnaInfoSchema extends ABSIO {

    //    字段名	重要性	属性	描述
    //    ID	    必须	String	身份证号码
    //    Name	    必须	String	身份证姓名
    //    Province	必须	String	省
    //    City	    必须	String	市
    //    District	必须	String	区
    //    Village	必须	String	村

    /**
     * 身份证号码
     */
    private String id;
    /**
     * 身份证姓名
     */
    private String name;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区
     */
    private String district;
    /**
     * 村
     */
    private String village;

    public RnaInfoSchema() {
    }

    public RnaInfoSchema(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public void readFrom(JSONObject json) throws JSONException {

        if (json == null) {
            return;
        }
        id = json.optString("ID");
        name = json.optString("Name");
        province = json.optString("Province");
        city = json.optString("City");
        district = json.optString("District");
        village = json.optString("Village");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        if (json == null) {
            return null;
        }
        JSONObject rnaInfo = new JSONObject();
        if (!TextUtils.isEmpty(id)) {
            rnaInfo.put("ID", id);
        }
        if (!TextUtils.isEmpty(name)) {
            rnaInfo.put("Name", name);
        }
        json.put("RNAInfo", rnaInfo);
        return json;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getVillage() {
        return village;
    }
}

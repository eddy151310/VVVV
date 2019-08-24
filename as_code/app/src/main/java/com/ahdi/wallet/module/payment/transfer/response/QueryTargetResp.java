package com.ahdi.wallet.module.payment.transfer.response;


import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/5.
 * @email zhao_zhaohe@163.com
 * <p>
 * 查询转账目标
 */
public class QueryTargetResp extends Response {

    //    字段名	重要性	类型	描述
    //    UID	    必须	String 用户ID（token）
    //    NNAME	    必须	String	用户昵称
    //    LNAME	    必须	String	用户登录账号（含隐藏码）
    //    RNAME 	必须	String	用户实名（含隐藏码）

    /**
     * 用户ID（token）
     */
    private String uID;
    /**
     * 用户昵称
     */
    private String nName;
    /**
     * 用户登录账号
     */
    private String lName;
    /**
     * 用户实名（含隐藏码）
     */
    private String rName;
    /**
     * 用户头像
     */
    private String avatar;

    private String ut;

    /**
     * 用户登录名（含隐藏码）
     */
    private String sName;


    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        ut = body.optString("UT");
        uID = body.optString("UID");
        nName = body.optString("NName");
        lName = body.optString("LName");
        sName = body.optString("SName");
        rName = body.optString("RName");
        avatar = body.optString("Avatar");
    }

    public String getuID() {
        return uID;
    }

    public String getnName() {
        return nName;
    }

    public String getlName() {
        return lName;
    }

    public String getrName() {
        return rName;
    }

    public String getUt() {
        return ut;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getsName() {
        return sName;
    }
}

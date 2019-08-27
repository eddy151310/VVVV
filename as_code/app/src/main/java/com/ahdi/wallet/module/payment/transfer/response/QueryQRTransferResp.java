package com.ahdi.wallet.module.payment.transfer.response;


import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/5.
 * @email zhao_zhaohe@163.com
 * <p>
 * 查询转账目标---扫码转账[包括扫描已输入金额的二维码]
 */
public class QueryQRTransferResp extends Response {

    //{"Code":"0","Data":{"Amount":"0","Currency":"IDR","To":{"UID":"1000217","NName":"LiuBin","LName":"123456"}}}

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
     * 目标用户头像
     */
    private String avatar;

    /**
     * 转账金额
     */
    private String Amount;

    private String Currency;
    private String ut;

    /**
     * 目标用户登陆名称（含隐含码）
     */
    private String sName;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        Amount = body.optString("Amount");
        Currency = body.optString("Currency");

        JSONObject jsonTo = body.optJSONObject("To");
        if (jsonTo == null) {
            return;
        } else {
            uID = jsonTo.optString("UID");
            nName = jsonTo.optString("NName");
            lName = jsonTo.optString("LName");
            sName = jsonTo.optString("SName");
            ut = jsonTo.optString("UT");
            avatar = jsonTo.optString("Avatar");
        }
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

    public String getAvatar() {
        return avatar;
    }

    public String getAmount() {
        return Amount;
    }

    public String getCurrency() {
        return Currency;
    }

    public String getUt() {
        return ut;
    }

    public String getsName() {
        return sName;
    }
}

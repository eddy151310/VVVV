package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class BindAccountReq extends Request {

    private String bankAccountNo;
    private String name;
    private String phone;
    private String bankCode;
    private String token;


    public BindAccountReq(String sid, String bankAccountNo, String name, String phone, String bankCode, String token) {
        super(sid);
        this.bankAccountNo = bankAccountNo;
        this.name = name;
        this.phone = phone;
        this.bankCode = bankCode;
        this.token = token;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;
        JSONObject body = new JSONObject();
        try {
            body.put("BankAccNo", bankAccountNo);
            body.put("Name", name);
            body.put("Tel", phone);
            body.put("BankCode", bankCode);
            body.put("Token", token);
            json.put(BODY, body);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("BindAccountReq", json.toString());
        return json;
    }

}

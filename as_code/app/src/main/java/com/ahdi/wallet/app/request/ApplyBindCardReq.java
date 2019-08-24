package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.cryptor.aes.AesKeyCryptor;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class ApplyBindCardReq extends Request {

    private static final String TAG = ApplyBindCardReq.class.getSimpleName();
    private String bank;
    private String token;


    public ApplyBindCardReq(String sid, String bank, String token) {
        super(sid);
        this.bank = bank;
        this.token = token;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;

        JSONObject body = new JSONObject();
        try {
            String aesValue = AesKeyCryptor.encodeAESPrivateKey();
            body.put("AES", aesValue);
            body.put("BankType", bank);
            body.put("Token",token);
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, "ApplyBindCardReq: " + json.toString());
        return json;
    }

}

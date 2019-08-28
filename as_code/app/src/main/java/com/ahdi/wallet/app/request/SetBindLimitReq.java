package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.cryptor.aes.AesKeyCryptor;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 修改绑卡限额
 */
public class SetBindLimitReq extends Request {

    private static final String TAG = SetBindLimitReq.class.getSimpleName();
    private String bid;


    public SetBindLimitReq(String sid, String bid) {
        super(sid);
        this.bid = bid;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;

        JSONObject body = new JSONObject();
        try {
            String aesValue = AesKeyCryptor.encodeAESPrivateKey();
            body.put("AES", aesValue);
            body.put("BID", bid);
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, "SetBindLimitReq: " + json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }

}

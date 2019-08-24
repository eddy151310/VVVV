package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/3/15.
 */
public class ScanCheckReq extends Request {

    private String scanCode;

    public ScanCheckReq(String sid, String scanCode) {
        super(sid);
        this.scanCode = scanCode;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;

        try {
            JSONObject body = new JSONObject();
            body.put("Code", scanCode);
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("ScanCheckReq", "ScanCheckReq: " + json.toString());
        return json;
    }

}

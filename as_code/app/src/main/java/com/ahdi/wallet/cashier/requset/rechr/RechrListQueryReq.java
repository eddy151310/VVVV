package com.ahdi.wallet.cashier.requset.rechr;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.wallet.app.schemas.TerminalInfoSchema;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class RechrListQueryReq extends Request {

    private String rechr;
    private String touchVer;
    private TerminalInfoSchema terminalInfoSchema;

    public RechrListQueryReq(String rechr, TerminalInfoSchema terminalInfoSchema, String touchVer, String sid) {
        super(sid);
        this.rechr = rechr;
        this.touchVer = touchVer;
        this.terminalInfoSchema = terminalInfoSchema;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;

        JSONObject body = new JSONObject();
        try {
            if (!TextUtils.isEmpty(rechr)) {
                body.put("Rechr", rechr);
            }

            if (terminalInfoSchema != null) {
                terminalInfoSchema.writeTo(body); //添加 terminalInfo
            }

            if (!TextUtils.isEmpty(touchVer)) {
                body.put("TouchVer", touchVer);
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("RechrListQueryReq", "RechrListQueryReq: " + json.toString());
        return json;
    }

}

package com.ahdi.wallet.cashier.requset.pay;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.wallet.app.schemas.LoginSchema;
import com.ahdi.wallet.app.schemas.TerminalInfoSchema;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class PricingRequest extends Request {

    private String CPOrder;
    private String CfgVersion;
    private TerminalInfoSchema terminalInfoSchema;
    private LoginSchema loginSchema;
    private String touchVer;

    /**
     * @param CPOrder 整个串不要urlencode
     */
    public PricingRequest(String CPOrder, String CfgVersion, TerminalInfoSchema terminalInfoSchema,
                          LoginSchema loginSchema, String touchVer, String sid) {
        super(sid);
        this.CPOrder = CPOrder;
        this.CfgVersion = CfgVersion;
        this.terminalInfoSchema = terminalInfoSchema;
        this.loginSchema = loginSchema;
        this.touchVer = touchVer;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null){
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("CPOrder", CPOrder);

            if (TextUtils.isEmpty(CfgVersion) && !"-1".equals(CfgVersion)) {
                body.put("CfgVer", CfgVersion);
            }

            if (terminalInfoSchema != null) {
                terminalInfoSchema.writeTo(body); //添加 terminalInfo
            }

            if (loginSchema != null) {
                loginSchema.writeTo(body); //添加 loginSchema
            }

            if (!TextUtils.isEmpty(touchVer)) {
                body.put("TouchVer", touchVer);
            }
            json.put(BODY, body);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("PricingRequest", json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }

}

package com.ahdi.wallet.cashier.requset.rechr;

import android.text.TextUtils;

import com.ahdi.wallet.cashier.bean.PayOrderBean;
import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.wallet.cashier.schemas.PayAuthSchema;
import com.ahdi.wallet.app.schemas.TerminalInfoSchema;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class RechrOrderReq extends Request {

    private String rechr;
    private String payEx;
    private String payInfo;
    private PayAuthSchema payAuthSchema;
    private TerminalInfoSchema terminalInfoSchema;
    private Integer payType;
    private String touchVer;

    public RechrOrderReq(PayOrderBean orderBean, String touchVer, String sid) {
        super(sid);
        this.touchVer = touchVer;
        if (orderBean != null) {
            this.rechr = orderBean.getRechrAmount();
            this.payEx = orderBean.getPayEx();
            this.payAuthSchema = orderBean.getPayAuthSchema();
            this.terminalInfoSchema = orderBean.getTerminalInfoSchema();
            this.payType = orderBean.getPayType();
            this.payInfo = orderBean.getPayInfo();
        }

    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;

        JSONObject body = new JSONObject();
        try {
            body.put("Rechr", rechr);

            body.put("PayEx", payEx);

            if (payAuthSchema != null) {
                payAuthSchema.writeTo(body);
            }

            if (terminalInfoSchema != null) {
                terminalInfoSchema.writeTo(body); //添加 terminalInfo
            }

            body.put("PayType", payType);

            if (!TextUtils.isEmpty(touchVer)) {
                body.put("TouchVer", touchVer);
            }

            if (!TextUtils.isEmpty(payInfo)) {
                body.put("PayInfo", payInfo);
            }
            json.put(BODY, body);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("RechrOrderReq", "RechrOrderReq: " + json.toString());
        return json;
    }

}

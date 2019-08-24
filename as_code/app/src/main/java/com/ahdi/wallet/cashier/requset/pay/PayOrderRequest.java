package com.ahdi.wallet.cashier.requset.pay;

import android.text.TextUtils;

import com.ahdi.wallet.cashier.bean.PayOrderBean;
import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.wallet.cashier.schemas.PayAuthSchema;
import com.ahdi.wallet.app.schemas.TerminalInfoSchema;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class PayOrderRequest extends Request {

    private String TT;
    private String payEx;
    private String payInfo;
    private PayAuthSchema payAuthSchema;
    private TerminalInfoSchema terminalInfoSchema;
    private Integer payType;
    private String touchVer;
    private Map<Integer, String> mPay;


    public PayOrderRequest(PayOrderBean orderBean, String touchVer, String sid) {
        super(sid);
        if (orderBean != null) {
            TT = orderBean.getTT();
            payEx = orderBean.getPayEx();
            payInfo = orderBean.getPayInfo();
            payAuthSchema = orderBean.getPayAuthSchema();
            terminalInfoSchema = orderBean.getTerminalInfoSchema();
            payType = orderBean.getPayType();
            mPay = orderBean.getMPay();
        }
        this.touchVer = touchVer;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null)
            return null;

        JSONObject body = new JSONObject();
        try {
            body.put("TT", TT);
            if (mPay != null && mPay.size() > 0) {
                JSONObject mPayJson = new JSONObject();
                for (Map.Entry<Integer, String> entry : mPay.entrySet()) {
                    mPayJson.put(String.valueOf(entry.getKey()), entry.getValue());
                }
                body.put("MPay", mPayJson);
            }

            if (!TextUtils.isEmpty(payEx)) {
                body.put("PayEx", payEx);
            }
            if (!TextUtils.isEmpty(payInfo)) {
                body.put("PayInfo", payInfo);
            }
            if (payAuthSchema != null) {
                payAuthSchema.writeTo(body);
            }
            if (terminalInfoSchema != null) {
                terminalInfoSchema.writeTo(body); //添加 terminalInfo
            }
            if (payType > -1) {
                body.put("PayType", payType);
            }
            if (!TextUtils.isEmpty(touchVer)) {
                body.put("TouchVer", touchVer);
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("PayOrderRequest", json.toString());
        return json;
    }

}

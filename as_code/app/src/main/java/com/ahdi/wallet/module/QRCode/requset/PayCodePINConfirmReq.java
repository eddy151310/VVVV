package com.ahdi.wallet.module.QRCode.requset;

import android.text.TextUtils;

import com.ahdi.wallet.module.QRCode.bean.PayCodePINConfirmBean;
import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.wallet.cashier.schemas.PayAuthSchema;
import com.ahdi.wallet.app.schemas.TerminalInfoSchema;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2018/1/4 下午5:56
 * Author: kay lau
 * Description:
 */
public class PayCodePINConfirmReq extends Request {

    private static final String TAG = PayCodePINConfirmReq.class.getSimpleName();

    private PayCodePINConfirmBean bean;
    private String touchVer;
    private String payAuthType;

    public PayCodePINConfirmReq(PayCodePINConfirmBean bean, String sid, String touchVer, String payAuthType) {
        super(sid);
        this.bean = bean;
        this.touchVer = touchVer;
        this.payAuthType = payAuthType;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {

        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            if (bean != null) {
                if (!TextUtils.isEmpty(bean.getTT())) {
                    body.put("TT", bean.getTT());
                }
                if (!TextUtils.isEmpty(bean.getOT())) {
                    body.put("OT", bean.getOT());
                }
                if (!TextUtils.isEmpty(bean.getPayEx())) {
                    body.put("PayEx", bean.getPayEx());
                }
                if (bean.getCancel() == 1) {
                    body.put("Cancel", bean.getCancel());
                } else {
                    String payAuth = bean.getPayAuth();
                    PayAuthSchema payAuthSchema = new PayAuthSchema(payAuthType, payAuth);
                    payAuthSchema.writeTo(body);
                }
            }
            TerminalInfoSchema terminalInfoSchema = new TerminalInfoSchema();
            terminalInfoSchema.writeTo(body);
            if (!TextUtils.isEmpty(touchVer)) {
                body.put("TouchVer", touchVer);
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LogUtil.e(TAG, "AuthCodeConfirm pay码 密码确认 or 用户主动取消 --- : " + json.toString());
        return json;
    }
}

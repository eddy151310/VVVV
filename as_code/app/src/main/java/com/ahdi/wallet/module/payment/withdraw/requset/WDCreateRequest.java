package com.ahdi.wallet.module.payment.withdraw.requset;

import android.text.TextUtils;

import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.wallet.app.schemas.TerminalInfoSchema;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/4/9.
 */

public class WDCreateRequest extends Request {

    private static final String TAG = WDCreateRequest.class.getSimpleName();

    private TerminalInfoSchema terminalInfo = new TerminalInfoSchema();
    private String target;
    private String amount;
    private String remark;
    private int want;//提现金额类型:0支付,1到账金额

    public WDCreateRequest(String sid, String target, int want, String amount, String remark) {
        super(sid);
        this.target = target;
        this.want = want;
        this.amount = amount;
        this.remark = remark;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            body = terminalInfo.writeTo(body); //添加 terminalInfo
            if (!TextUtils.isEmpty(target)) {
                body.put("Target", target);
            }
            body.put("Want", want);
            if (!TextUtils.isEmpty(amount)) {
                body.put("Amount", amount);
            }
            body.put("Currency", ConfigCountry.KEY_CURRENCY);
            if (!TextUtils.isEmpty(remark)) {
                body.put("Remark", remark);
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, json.toString());
        return json;
    }
}

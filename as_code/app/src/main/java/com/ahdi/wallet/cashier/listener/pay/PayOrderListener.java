package com.ahdi.wallet.cashier.listener.pay;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.wallet.cashier.PayCashierSdk;
import com.ahdi.wallet.cashier.callback.PaymentSdkCallBack;
import com.ahdi.wallet.cashier.main.PayCashierMain;
import com.ahdi.wallet.cashier.response.pay.PayOrderResponse;
import com.ahdi.wallet.app.schemas.TouchSchema;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.fingerprint.FingerprintSDK;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;

import org.json.JSONObject;

/**
 * Date: 2018/1/5 下午5:09
 * Author: kay lau
 * Description:
 */
public class PayOrderListener implements HttpReqTaskListener {

    private static final String TAG = PayOrderListener.class.getSimpleName();

    private Context context;
    private PaymentSdkCallBack callBack;
    private String iv;

    public PayOrderListener(Context context, String iv, PaymentSdkCallBack callBack) {
        this.context = context;
        this.callBack = callBack;
        this.iv = iv;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        if (callBack == null) {
            LogUtil.e(TAG, "PayOrderListener bankCardCallBack = null");
            return;
        }
        PayOrderResponse resp = PayOrderResponse.decodeJson(PayOrderResponse.class, json);
        if (resp != null) {
            if (TextUtils.equals(PayCashierSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().RetCode)) {
                if (!TextUtils.isEmpty(resp.PayParam)) {
                    // callback关闭支付密码验证界面, 打开 bca otp 界面
                    callBack.onResult(PayCashierSdk.LOCAL_PAY_OTP_VERIFY, resp.getmHeader().ErrMsg, json);

                } else {
                    TouchSchema touchSchema = resp.getTouchSchema();
                    if (touchSchema != null) {
                        String lName = AppGlobalUtil.getInstance().getLName(context);
                        TouchIDStateUtil.saveTouchIDPayKey(context, lName, touchSchema.Key);
                        TouchIDStateUtil.saveTouchIDPayVer(context, lName, touchSchema.Ver);
                    }
                    if (!TextUtils.isEmpty(iv)) {
                        FingerprintSDK.putIv(context, iv);
                    }
                    LogUtil.e(TAG, "下单成功 iv: " + iv);
                    // 支付结果查询
                    PayCashierMain.getInstance().payResultQuery(context, resp.OT, resp.TT, callBack);
                }
            } else {
                callBack.onResult(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, json);
            }
        } else {
            callBack.onResult(PayCashierSdk.LOCAL_PAY_SYSTEM_EXCEPTION, PayCashierMain.getInstance().default_error, null);
            LogUtil.e(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        if (callBack != null) {
            String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
            callBack.onResult(PayCashierSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json);
        } else {
            LogUtil.e(TAG, "bankCardCallBack = null");
        }
    }
}

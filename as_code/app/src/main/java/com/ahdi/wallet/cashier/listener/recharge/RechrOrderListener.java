package com.ahdi.wallet.cashier.listener.recharge;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.wallet.cashier.PayCashierSdk;
import com.ahdi.wallet.cashier.RechrCashierSdk;
import com.ahdi.wallet.cashier.main.RechrCashierMain;
import com.ahdi.wallet.cashier.callback.RechrCallBack;
import com.ahdi.wallet.cashier.response.rechr.RechrOrderRsp;
import com.ahdi.wallet.app.schemas.TouchSchema;
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
public class RechrOrderListener implements HttpReqTaskListener {

    private static final String TAG = RechrOrderListener.class.getSimpleName();

    private Context context;
    private RechrCallBack callBack;
    private String iv;

    public RechrOrderListener(Context context, String iv, RechrCallBack callBack) {
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
        RechrOrderRsp resp = RechrOrderRsp.decodeJson(RechrOrderRsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(PayCashierSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().RetCode)) {
                if (!TextUtils.isEmpty(resp.PayParam)) {
                    // callback关闭支付密码验证界面, 打开 bca otp 界面
                    callBack.onResult(RechrCashierSdk.LOCAL_PAY_OTP_VERIFY, resp.getmHeader().ErrMsg, json, resp.OT, resp.orderID);

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
                    // 充值支付结果查询
                    RechrCashierMain.getInstance().rechargePayResultQuery(context, resp.OT, resp.orderID, callBack);
                }
            } else {
                onCallback(json, resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, resp.orderID);
            }
        } else {
            onCallback(json, RechrCashierSdk.LOCAL_PAY_SYSTEM_EXCEPTION, RechrCashierMain.getInstance().default_error, "");
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    private void onCallback(JSONObject json, String retCode, String errMsg, String orderID) {
        if (callBack != null) {
            callBack.onResult(retCode, errMsg, json, "", orderID);
        } else {
            LogUtil.e(TAG, "RechrOrderListener callback = null");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        onCallback(json, RechrCashierSdk.LOCAL_PAY_NETWORK_EXCEPTION, "", "");
    }
}

package com.ahdi.wallet.cashier;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.wallet.cashier.callback.PaymentSdkCallBack;
import com.ahdi.wallet.cashier.main.PayCashierMain;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;

/**
 * Date: 2018/1/4 上午11:51
 * Author: kay lau
 * Description:
 */
public class PayCashierSdk {

    public static final String TAG = PayCashierSdk.class.getSimpleName();

    private static long lastClickTime;

    public static final String LOCAL_PAY_SUCCESS = Constants.RET_CODE_SUCCESS;   // 支付完成

    public static final String LOCAL_PAY_PARAM_ERROR = "-3";                     // 参数错误

    public static final String LOCAL_PAY_USER_CANCEL = "-4";                     // 用户取消, 关闭收银台

    public static final String LOCAL_PAY_NETWORK_EXCEPTION = Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION;               // 网络异常

    public static final String LOCAL_PAY_SYSTEM_EXCEPTION = "-6";                // 系统响应解析异常

    public static final String LOCAL_PAY_QUERY_CANCEL = "-7";                    // 查询结果后取消 继续查询

    public static final String LOCAL_PAY_OTP_VERIFY = "-8";                      // 下单返回需要bca otp

    public static final String USER_CANCEL = "Payment user cancel";              // 用户手动取消


    /**
     * 是否可以再次加载 防止多次调用接口
     *
     * @return
     */
    private static boolean isCanLoading() {

        try {
            long time = System.currentTimeMillis();
            long offSetTime = time - lastClickTime;
            if (Math.abs(offSetTime) > 500) {
                lastClickTime = time;
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private static boolean checkContextAndCallback(Context context, PaymentSdkCallBack callBack) {
        if (context == null) {
            LogUtil.d(TAG, "context can't be empty");
            return true;
        }

        if (callBack == null) {
            LogUtil.d(TAG, "callback can't be empty");
            return true;
        }
        return false;
    }

    /**
     * 支付 [转账 发红包 扫码支付]
     *
     * @param context
     * @param params
     * @param sid
     * @param callBack
     */
    public static void startPay(Context context, String params, String sid, int from, PaymentSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!isCanLoading()) {
            LogUtil.e(TAG, "Please do not repeat submit");
            return;
        }
        if (TextUtils.isEmpty(params)) {
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "params[CPOrder] can't be empty", null);
            return;
        }
        if (TextUtils.isEmpty(sid)) {
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "sid can't be empty", null);
            return;
        }
        PayCashierMain.getInstance().callBack = callBack;
        PayCashierMain.getInstance().startPay(context, params, sid, from);
    }

    /**
     * 二维码使用后, 查询支付结果
     *
     * @param context
     * @param tt
     * @param ot
     * @param sid
     * @param callBack
     */
    public static void payQRResultQuery(Context context, String tt, String ot, String sid, PaymentSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(tt)) {
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "transToken can't be empty---(local)", null);
            return;
        }
        if (TextUtils.isEmpty(ot)) {
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "OT can't be empty---(local)", null);
            return;
        }
        if (TextUtils.isEmpty(sid)) {
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "sid can't be empty", null);
            return;
        }
        PayCashierMain.getInstance().callBack = callBack;
        PayCashierMain.getInstance().payQRResultQuery(context, tt, ot, sid);
    }
}
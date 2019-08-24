package com.ahdi.wallet.module.payment.topup;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.wallet.module.payment.topup.callback.TopUpCallBack;
import com.ahdi.wallet.module.payment.topup.main.TopUpMain;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;

/**
 * Date: 2018/4/25 上午11:09
 * Author: kay lau
 * Description:
 */
public class TopUpSdk {

    public static final String TAG = TopUpSdk.class.getSimpleName();

    private static long lastClickTime;

    public static final String LOCAL_PAY_SUCCESS = Constants.RET_CODE_SUCCESS;   // 支付完成

    public static final String LOCAL_PAY_PARAM_ERROR = "-3";                     // 参数错误

    public static final String LOCAL_PAY_NETWORK_EXCEPTION = Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION;               // 网络异常

    public static final String LOCAL_PAY_SYSTEM_EXCEPTION = "-6";                // 系统响应解析异常



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

    private static boolean checkContextAndCallback(Context context, TopUpCallBack callBack) {
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
     * 查询充值额度
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public static void queryRechargeQuota(Context context, String sid, TopUpCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!isCanLoading()) {
            LogUtil.e(TAG, "Please do not repeat submit");
            return;
        }
        if (TextUtils.isEmpty(sid)) {
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "sid can't be empty", null, "");
            return;
        }
        TopUpMain.getInstance().callBack = callBack;
        TopUpMain.getInstance().queryRechargeQuota(context, sid);
    }

    /**
     * 查询充值结果
     *
     * @param context
     * @param ot
     * @param sid
     * @param callBack
     */
    public static void rechargeResultQuery(Context context, String ot, String sid, TopUpCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!isCanLoading()) {
            LogUtil.e(TAG, "Please do not repeat submit");
            return;
        }
        if (TextUtils.isEmpty(sid)) {
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "sid can't be empty", null, "");
            return;
        }
        if (TextUtils.isEmpty(ot)) {
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "OT can't be empty", null, "");
            return;
        }
        TopUpMain.getInstance().callBack = callBack;
        TopUpMain.getInstance().rechargeResultQuery(context, ot, sid);
    }

    /**
     * 充值类型查询
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public static void rechrTypeQuery(Context context, String sid, TopUpCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(sid)) {
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "sid can't be empty", null, "");
            return;
        }
        TopUpMain.getInstance().callBack = callBack;
        TopUpMain.getInstance().rechrTypeQuery(context, sid);
    }
}

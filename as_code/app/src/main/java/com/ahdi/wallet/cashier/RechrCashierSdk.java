package com.ahdi.wallet.cashier;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.cashier.main.RechrCashierMain;
import com.ahdi.wallet.module.payment.topup.callback.TopUpCallBack;

/**
 * Date: 2019/6/24 下午3:37
 * Author: kay lau
 * Description:
 */
public class RechrCashierSdk {

    public static final String TAG = RechrCashierSdk.class.getSimpleName();

    private static long lastClickTime;

    public static final String LOCAL_PAY_SUCCESS = Constants.RET_CODE_SUCCESS;   // 支付完成

    public static final String LOCAL_PAY_PARAM_ERROR = "-3";                     // 参数错误

    public static final String LOCAL_PAY_USER_CANCEL = "-4";                     // 用户取消, 关闭收银台

    public static final String LOCAL_PAY_NETWORK_EXCEPTION = Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION;               // 网络异常

    public static final String LOCAL_PAY_SYSTEM_EXCEPTION = "-6";                // 系统响应解析异常

    public static final String LOCAL_PAY_QUERY_CANCEL = "-7";                    // 查询结果后取消 继续查询

    public static final String LOCAL_PAY_OTP_VERIFY = "-8";                      // 下单返回需要bca otp

    public static final String USER_CANCEL = "Recharge user cancel";             // 用户手动取消



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

    /**
     * 充值
     *
     * @param context
     * @param rechr
     * @param sid
     * @param callBack
     */
    public static void recharge(Context context, String rechr, String sid, TopUpCallBack callBack) {

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
        if (TextUtils.isEmpty(rechr)) {
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "rechr amount can't be empty", null, "");
            return;
        }
        RechrCashierMain.getInstance().callBack = callBack;
        RechrCashierMain.getInstance().recharge(context, rechr, sid);
    }
}

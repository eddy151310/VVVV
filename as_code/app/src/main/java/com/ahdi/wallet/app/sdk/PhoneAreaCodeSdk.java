package com.ahdi.wallet.app.sdk;

import android.content.Context;

import com.ahdi.wallet.app.callback.PhoneAreaCodeCallBack;
import com.ahdi.wallet.app.main.PhoneAreaCodeSdkMain;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;

/**
 * @author zhaohe
 */
public class PhoneAreaCodeSdk {

    public static final String TAG = PhoneAreaCodeSdk.class.getSimpleName();

    public static final String LOCAL_SUCCESS = Constants.RET_CODE_SUCCESS;   // 支付完成

    public static final String LOCAL_PARAM_ERROR = "-3";                     // 参数错误

    public static final String LOCAL_USER_CANCEL = "-4";                     // 用户取消, 关闭收银台

    public static final String LOCAL_NETWORK_EXCEPTION = Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION;               // 网络异常

    public static final String LOCAL_SYSTEM_EXCEPTION = "-6";                // 系统响应解析异常

    public static final String LOCAL_QUERY_CANCEL = "-7";                    // 查询结果后取消 继续查询


    private static boolean checkContextAndCallback(Context context, PhoneAreaCodeCallBack callBack) {
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
     * 选择电话区号
     *
     * @param context
     * @param callBack
     */
    public static void selectAreaCode(Context context, PhoneAreaCodeCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        PhoneAreaCodeSdkMain.getInstance().callBack = callBack;
        PhoneAreaCodeSdkMain.getInstance().getAreaCodeData(context);
    }
}
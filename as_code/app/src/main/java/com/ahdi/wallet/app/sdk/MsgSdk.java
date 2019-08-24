package com.ahdi.wallet.app.sdk;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.wallet.app.callback.MsgSdkCallBack;
import com.ahdi.wallet.app.main.MessageSdkMain;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;

/**
 * Date: 2018/1/4 上午11:51
 * Author: kay lau
 * Description:
 */
public class MsgSdk {

    public static final String TAG = MsgSdk.class.getSimpleName();

    public static final String LOCAL_SUCCESS = Constants.RET_CODE_SUCCESS;   // 支付完成

    public static final String LOCAL_PARAM_ERROR = "-3";                     // 参数错误

    public static final String LOCAL_NETWORK_EXCEPTION = Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION;               // 网络异常

    public static final String LOCAL_SYSTEM_EXCEPTION = "-6";                // 系统响应解析异常


    private static boolean checkContextAndCallback(Context context, MsgSdkCallBack callBack) {
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

    private static boolean checkSid(String sid, MsgSdkCallBack callBack) {
        if (TextUtils.isEmpty(sid)) {
            LogUtil.d(TAG, "sid can't be empty");
            callBack.onResult(LOCAL_PARAM_ERROR, "sid can't be empty", null);
            return false;
        }
        return true;
    }

    public static void getMsgHomeUrl(Context context, String sid, MsgSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        MessageSdkMain.getInstance().callBack = callBack;
        MessageSdkMain.getInstance().getMsgHomeUrl(context, sid);
    }
}
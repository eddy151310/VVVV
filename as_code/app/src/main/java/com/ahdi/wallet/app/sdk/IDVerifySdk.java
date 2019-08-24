package com.ahdi.wallet.app.sdk;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.wallet.app.callback.IDVerifyCallBack;
import com.ahdi.wallet.app.main.IDVerifySdkMain;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;

/**
 * 实名认证sdk入口
 * @author zhaohe
 */
public class IDVerifySdk {

    public static final String TAG = IDVerifySdk.class.getSimpleName();

    public static final String LOCAL_SUCCESS = Constants.RET_CODE_SUCCESS;
    /**
     *  参数错误
     */
    public static final String LOCAL_PARAM_ERROR = "-3";
    /**
     * 用户取消
     */
    public static final String LOCAL_USER_CANCEL = "-4";
    /**
     * 网络异常
     */
    public static final String LOCAL_NETWORK_EXCEPTION = Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION;
    /**
     *  系统响应解析异常
     */
    public static final String LOCAL_SYSTEM_EXCEPTION = "-6";
    /**
     *  失败
     */
    public static final String LOCAL_FAIL = "FAIL";
    /**
     *  等待中
     */
    public static final String LOCAL_WAIT = "WAIT";


    private static boolean checkContextAndCallback(Context context, IDVerifyCallBack callBack) {
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
     * 实名认证审核结果查询
     * @param context
     * @param sid
     * @param showSuccessEnding  true Me界面详情页面
     * @param callBack
     */
    public static void auditQR(Context context,String sid, boolean showSuccessEnding, IDVerifyCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(sid)) {
            callBack.onResult(LOCAL_PARAM_ERROR, "sid can't be empty", null);
            return;
        }
        IDVerifySdkMain.getInstance().callBack = callBack;
        IDVerifySdkMain.getInstance().showSuccessEnding = showSuccessEnding;
        IDVerifySdkMain.getInstance().onAuditQR(context, sid);
    }

}
package com.ahdi.wallet.app.sdk;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.wallet.app.bean.BindAccountBean;
import com.ahdi.wallet.app.callback.BankAccountSdkCallBack;
import com.ahdi.wallet.app.main.BankAccountSdkMain;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;


public class BankAccountSdk {

    public static final String TAG = BankAccountSdk.class.getSimpleName();

    public static final String LOCAL_PAY_SUCCESS = Constants.RET_CODE_SUCCESS;   // 支付完成

    public static final String LOCAL_PAY_PARAM_ERROR = "-3";                     // 参数错误

    public static final String LOCAL_PAY_USER_CANCEL = "-4";                     // 用户取消, 关闭收银台

    public static final String LOCAL_PAY_NETWORK_EXCEPTION = Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION;               // 网络异常

    public static final String LOCAL_PAY_SYSTEM_EXCEPTION = "-6";                // 系统响应解析异常

    public static final String LOCAL_PAY_QUERY_CANCEL = "-7";                    // 查询结果后取消 继续查询

    private static boolean checkContextAndCallback(Context context, BankAccountSdkCallBack callBack) {
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

    private static boolean checkSid(String sid, BankAccountSdkCallBack callBack) {
        if (TextUtils.isEmpty(sid)) {
            LogUtil.d(TAG, "sid can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "sid can't be empty", null);
            return false;
        }
        return true;
    }

    /**
     * 查询绑卡信息
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public static void queryBindAccountInfo(Context context, String sid, BankAccountSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        BankAccountSdkMain.getInstance().queryBindCardInfo(context, sid, callBack);
    }

    /**
     * 申请添加银行账户
     *
     * @param context
     * @param callBack
     */
    public static void bindAccount(Context context, String sid, BindAccountBean bean, BankAccountSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        if (bean == null) {
            LogUtil.d(TAG, "params can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "params can't be empty", null);
            return;
        }
        BankAccountSdkMain.getInstance().callBack = callBack;
        BankAccountSdkMain.getInstance().bindAccount(context, sid, bean, callBack);
    }

    /**
     * 解绑银行账户
     *
     * @param context
     * @param ST
     * @param callBack
     */
    public static void unBindAccount(Context context, String sid, String ST, BankAccountSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(ST)) {
            LogUtil.d(TAG, "params can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "params can't be empty", null);
            return;
        }
        BankAccountSdkMain.getInstance().callBack = callBack;
        BankAccountSdkMain.getInstance().unBindCard(context, sid, ST);
    }

    /**
     * 查询绑定账户银行列表
     *
     * @param context
     * @param callBack
     */
    public static void queryBankAccountList(Context context, String sid, long time, BankAccountSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        BankAccountSdkMain.getInstance().callBack = callBack;
        BankAccountSdkMain.getInstance().queryBankAccountList(context, sid, time);
    }

}
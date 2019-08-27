package com.ahdi.wallet.app.sdk;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.cryptor.aes.AesKeyCryptor;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.app.callback.AccountSdkCallBack;
import com.ahdi.wallet.app.main.AccountSdkMain;
import com.ahdi.wallet.app.ui.activities.other.CheckPayPwdActivity;


/**
 * Date: 2018/1/4 上午11:51
 * Author: kay lau
 * Description:
 */
public class AccountSdk {

    public static final String TAG = AccountSdk.class.getSimpleName();

    public static final String LOCAL_PAY_SUCCESS = Constants.RET_CODE_SUCCESS;   // 成功

    public static final String LOCAL_PAY_PARAM_ERROR = "-3";                     // 参数错误

    public static final String LOCAL_PAY_USER_CANCEL = "-4";                     // 用户取消, 关闭收银台

    public static final String LOCAL_PAY_NETWORK_EXCEPTION = Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION;               // 网络异常

    public static final String LOCAL_PAY_SYSTEM_EXCEPTION = "-6";                // 系统响应解析异常

    public static final String LOCAL_PAY_QUERY_CANCEL = "-7";                    // 查询结果后取消 继续查询


    /**
     * 密码类型
     */
    public static final int PWD_CHECK_TTYPE_MODIFY = 0;              // 修改支付密码
    public static final int PWD_CHECK_TTYPE_BINDBANKACCOUNT = 5;     // 绑定提现账户
    public static final int PWD_CHECK_TTYPE_NOT = 2;                 // 无

    /**
     * 是否可以再次加载 防止多次调用接口
     *
     * @return
     */

    private static boolean checkContextAndCallback(Context context, AccountSdkCallBack callBack) {
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

    private static boolean checkSid(String sid, AccountSdkCallBack callBack) {
        if (TextUtils.isEmpty(sid)) {
            LogUtil.d(TAG, "sid can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "sid can't be empty", null);
            return false;
        }
        return true;
    }

    /**
     * 查询余额
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public static void queryBalance(Context context, String sid, AccountSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        AccountSdkMain.getInstance().callBack = callBack;
        AccountSdkMain.getInstance().queryBalance(context, sid);
    }

    /**
     * 设置支付密码信息
     *
     * @param context
     * @param PayPwd     支付密码 需要加密
     * @param NoPwdLimit 免密额度 必须 > 0 可选
     * @param callBack
     */
    public static void setPayPWD(Context context, String sid, String PayPwd, long NoPwdLimit, AccountSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }

        if (TextUtils.isEmpty(PayPwd)) {
            LogUtil.d(TAG, "PayPwd can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "PayPwd can't be empty", null);
            return;
        }
        AccountSdkMain.getInstance().callBack = callBack;
        AccountSdkMain.getInstance().setPayPWD(context, sid, PayPwd, NoPwdLimit);
    }

    /**
     * 修改支付密码
     *
     * @param context
     * @param PayPwd
     * @param token
     * @param NoPwdLimit
     * @param callBack
     */
    public static void resetPayPWD(Context context, String sid, String PayPwd, String token, long NoPwdLimit, AccountSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }

        if (TextUtils.isEmpty(PayPwd)) {
            LogUtil.d(TAG, "PayPwd can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "PayPwd can't be empty", null);
            return;
        }
        if (TextUtils.isEmpty(token)) {
            LogUtil.d(TAG, "token can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "token can't be empty", null);
            return;
        }
        AccountSdkMain.getInstance().callBack = callBack;
        AccountSdkMain.getInstance().resetPayPWD(context, sid, AesKeyCryptor.encodePayPwd(PayPwd), token, NoPwdLimit);
    }


    /**
     * new---
     * 验证支付密码
     *
     * @param context
     * @param sid
     * @param TType
     * @param callBack
     */
    public static void checkPayPwd(Context context, String sid, int TType, AccountSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        AccountSdkMain.getInstance().checkPayPwdCallback = callBack;

        Intent intent = new Intent(context, CheckPayPwdActivity.class);
        intent.putExtra(Constants.LOCAL_KEY_SID, sid);
        intent.putExtra(Constants.LOCAL_TTYPE_KEY, TType);
        context.startActivity(intent);
    }


    /**
     * 用户消息概要
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public static void profile(Context context, String sid, AccountSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        AccountSdkMain.getInstance().profile(context, sid, callBack);
    }



    /**
     * 添加银行账户流程: 校验支付密码-->申请添加银行账户
     *
     * @param context
     * @param sid
     * @param pwdType
     * @param callBack
     */
    public static void addBankAccount(Context context, String sid, int pwdType, AccountSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        AccountSdkMain.getInstance().addBankAccount(context, pwdType, sid, callBack);
    }

    /**
     * 账单列表
     *
     * @param context
     * @param sid
     * @param last
     * @param type
     * @param month
     * @param callBack
     */
    public static AsyncTask<Void, Void, Void> getTransList(Context context, String sid, String last, int type, long month, AccountSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return null;
        }
        if (!checkSid(sid, callBack)) {
            return null;
        }
        return AccountSdkMain.getInstance().getTransList(context, sid, last, type, month, callBack);
    }

    /**
     * 开启指纹支付
     *
     * @param context
     * @param publicKey
     * @param payPwdCipher
     * @param sign
     * @param sid
     */
    public static void openTouchIDPay(Context context, String publicKey, String payPwdCipher, String sign, String sid, AccountSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(publicKey) || TextUtils.isEmpty(payPwdCipher) || TextUtils.isEmpty(sign)) {
            LogUtil.d(TAG, "param can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "param can't be empty", null);
            return;
        }
        AccountSdkMain.getInstance().openTouchIDPay(context, publicKey, payPwdCipher, sign, sid, callBack);
    }

    /**
     * 关闭指纹支付
     *
     * @param sid
     */
    public static void closeTouchIDPay(String sid, AccountSdkCallBack callBack) {
        if (!checkSid(sid, callBack)) {
            return;
        }
        AccountSdkMain.getInstance().closeTouchIDPay(sid);
    }

}
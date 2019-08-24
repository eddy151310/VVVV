package com.ahdi.wallet.bca;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.wallet.bca.callback.BcaBankCardCallBack;
import com.ahdi.wallet.bca.callback.BcaOTPCallBack;
import com.ahdi.wallet.bca.main.BcaSdkMain;
import com.ahdi.wallet.app.schemas.PayBindSchema;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;

/**
 * @author xiaoniu
 * bca
 */
public class BcaSdk {

    public static final String TAG = BcaSdk.class.getSimpleName();

    public static final String LOCAL_PAY_PARAM_ERROR = "-3";// 参数错误

    public static final String LOCAL_PAY_USER_CANCEL = "-4";                     // 用户取消, 关闭收银台

    public static final String LOCAL_PAY_NETWORK_EXCEPTION = Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION;// 网络异常

    public static final String LOCAL_PAY_SYSTEM_EXCEPTION = "-6";                // 系统响应解析异常

    private static boolean checkContextAndCallback(Context context, BcaBankCardCallBack callBack) {
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

    private static boolean checkSid(String sid, BcaBankCardCallBack callBack) {
        if (TextUtils.isEmpty(sid)) {
            LogUtil.d(TAG, "sid can't be empty");
            callBack.onFail(LOCAL_PAY_PARAM_ERROR);
            return false;
        }
        return true;
    }

    /**
     * bca绑卡
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public static void bcaBindCard(Context context, String sid, PayBindSchema payBindSchema, BcaBankCardCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        if (payBindSchema == null) {
            LogUtil.d(TAG, "payBindSchema = null");
            callBack.onFail(LOCAL_PAY_PARAM_ERROR);
            return;
        }
        BcaSdkMain.getInstance().bankCardCallBack = callBack;
        BcaSdkMain.getInstance().bcaSDKPage(context, sid, "", payBindSchema, Constants.LOCAL_BCA_PAGE_BIND_CARD);
    }

    /**
     * bca修改限额
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public static void bcaLimit(Context context, String sid, String bid, PayBindSchema payBindSchema, BcaBankCardCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(bid)) {
            LogUtil.d(TAG, "bid = null");
            callBack.onFail(LOCAL_PAY_PARAM_ERROR);
            return;
        }
        if (payBindSchema == null) {
            LogUtil.d(TAG, "payBindSchema = null");
            callBack.onFail(LOCAL_PAY_PARAM_ERROR);
            return;
        }
        BcaSdkMain.getInstance().bankCardCallBack = callBack;
        BcaSdkMain.getInstance().bcaSDKPage(context, sid, bid, payBindSchema, Constants.LOCAL_BCA_PAGE_MODIFY_LIMIT);
    }

    /**
     * BCA银行卡支付, OTP 下发短信验证码
     *
     * @param context
     * @param sid
     * @param msisdnId
     * @param payEx
     * @param payInfo
     * @param
     */
    public static void bcaOTPSendVcode(Context context, String sid, String msisdnId, String payEx, String payInfo, BcaOTPCallBack callBack) {
        if (context == null) {
            LogUtil.d(TAG, "context can't be empty");
            return;
        }
        if (callBack == null) {
            LogUtil.d(TAG, "callback can't be empty");
            return;
        }
        if (TextUtils.isEmpty(msisdnId)) {
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "msisdnId can't be empty---(local)", null);
            return;
        }
        if (TextUtils.isEmpty(payEx)) {
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "payEx can't be empty---(local)", null);
            return;
        }
        if (TextUtils.isEmpty(payInfo)) {
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "payInfo can't be empty---(local)", null);
            return;
        }
        if (TextUtils.isEmpty(sid)) {
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "sid can't be empty", null);
            return;
        }
        BcaSdkMain.getInstance().bcaOTPSendVcode(context, sid, msisdnId, payEx, payInfo, callBack);
    }

    /**
     * otp支付下单
     *
     * @param context
     * @param sid
     * @param payEx
     * @param payInfo
     * @param channelID
     * @param callBack
     */
    public static void bcaOTPOrder(Context context, String sid, String payEx, String payInfo, int channelID, BcaOTPCallBack callBack) {
        if (context == null) {
            LogUtil.d(TAG, "context can't be empty");
            return;
        }
        if (callBack == null) {
            LogUtil.d(TAG, "callback can't be empty");
            return;
        }
        if (TextUtils.isEmpty(sid)) {
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "sid can't be empty", null);
            return;
        }
        if (TextUtils.isEmpty(payEx)) {
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "payEx can't be empty---(local)", null);
            return;
        }
        if (TextUtils.isEmpty(payInfo)) {
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "payInfo can't be empty---(local)", null);
            return;
        }
        BcaSdkMain.getInstance().otpOrder(context, sid, payEx, payInfo, channelID, callBack);
    }
}
package com.ahdi.wallet.app.sdk;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.ahdi.wallet.app.callback.VoucherSdkCallBack;
import com.ahdi.wallet.app.main.VoucherSdkMain;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;

/**
 * @author xiaoniu
 * 优惠券相关的接口
 */
public class VoucherSdk {
    private static final String TAG = "VoucherSdk";

    public static final String LOCAL_PARAM_ERROR = "-3";// 参数错误
    public static final String LOCAL_NETWORK_EXCEPTION = Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION;// 网络异常
    public static final String LOCAL_SYSTEM_EXCEPTION = "-6";                // 系统响应解析异常

    private static boolean checkContextAndCallback(Context context, VoucherSdkCallBack callBack) {
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

    private static boolean checkSid(String sid, VoucherSdkCallBack callBack) {
        if (TextUtils.isEmpty(sid)) {
            LogUtil.d(TAG, "sid can't be empty");
            callBack.onResult(LOCAL_PARAM_ERROR, "sid can't be empty", null);
            return false;
        }
        return true;
    }

    /**
     * 优惠券列表
     * @param context
     * @param sid
     * @param id 优惠券id
     * @param cType 优惠券类型
     * @param callBack
     */
    public static AsyncTask voucherList(Context context, String sid, String id, int cType, VoucherSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return null;
        }
        if (!checkSid(sid, callBack)) {
            return null;
        }
        if (cType != 0 && cType != 1){
            LogUtil.d(TAG, "cTyp must is 0|1");
            callBack.onResult(LOCAL_PARAM_ERROR, "cTyp must is 0|1", null);
            return null;
        }
        return VoucherSdkMain.getInstance().voucherList(context, sid, id, cType, callBack);
    }

    /**
     *
     * @param context
     * @param sid
     * @param id
     * @param callBack
     */
    public static void voucherDetail(Context context, String sid, String id, VoucherSdkCallBack callBack){

        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }

        if (TextUtils.isEmpty(id)){
            LogUtil.d(TAG, "id can't be empty");
            callBack.onResult(LOCAL_PARAM_ERROR, "id can't be empty", null);
            return;
        }

        VoucherSdkMain.getInstance().voucherDetail(context, sid, id, callBack);
    }
}
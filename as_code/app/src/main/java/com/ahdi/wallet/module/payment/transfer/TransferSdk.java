package com.ahdi.wallet.module.payment.transfer;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.module.payment.transfer.bean.TABean;
import com.ahdi.wallet.module.payment.transfer.callback.TransferResultCallBack;
import com.ahdi.wallet.module.payment.transfer.main.TransferMain;

/**
 * @author admin
 * 转账sdk入口
 */
public class TransferSdk {

    private static final String TAG = TransferSdk.class.getSimpleName();
    /**
     * 上次点击时间
     */
    private static long lastClickTime;

    public static final String LOCAL_PAY_NETWORK_EXCEPTION = Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION;               // 网络异常

    public static final String LOCAL_PAY_SUCCESS = Constants.RET_CODE_SUCCESS;   // 支付完成

    public static final String LOCAL_PAY_PARAM_ERROR = "-3";                     // 参数错误

    public static final String LOCAL_PAY_USER_CANCEL = "-4";                     // 用户取消, 关闭收银台

    public static final String LOCAL_PAY_SYSTEM_EXCEPTION = "-6";                // 系统响应解析异常

    public static final String LOCAL_PAY_QUERY_CANCEL = "-7";                    // 查询结果后取消 继续查询

    /**
     * 创建转账
     *
     * @param context
     * @param bean     转账实体
     * @param sid
     * @param callBack
     */
    public static void createTransfer(Context context, TABean bean, String sid, TransferResultCallBack callBack) {

        if (!checkContextAndCallBack(context, callBack)) {
            return;
        }
        if (bean == null) {
            LogUtil.e(TAG, "TABean cannot be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "Parameters cannot be empty", null, "");
            return;
        }
        if (!checkSID(sid, callBack)) {
            return;
        }

        if (!isCanLoading()) {
            LogUtil.e(TAG, "Please do not repeat submit");
            return;
        }
        LogUtil.e(TAG, "开始创建转账: " + bean.toString());
        TransferMain.getInstance().callBack = callBack;
        TransferMain.getInstance().createTransfer(context, bean, sid);
    }

    /**
     * 查询转账目标
     *
     * @param context
     * @param lName
     * @param sid
     * @param callBack
     */
    public static void queryTransferTarget(Context context, String lName, String sid, TransferResultCallBack callBack) {

        if (!checkContextAndCallBack(context, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(lName)) {
            LogUtil.e(TAG, "邮箱或者手机号码 cannot be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "Parameters cannot be empty", null, "");
            return;
        }
        if (!checkSID(sid, callBack)) {
            return;
        }

        if (!isCanLoading()) {
            LogUtil.e(TAG, "Please do not repeat submit");
            return;
        }
        LogUtil.e(TAG, "查询转账目标，q = " + lName);
        TransferMain.getInstance().callBack = callBack;
        TransferMain.getInstance().queryTransferTarget(context, lName, sid);
    }

    /**
     * 查询二维码转账目标 get请求
     *
     * @param context
     * @param callBack
     */
    public static void onQRTransferTarget(Context context, String param, TransferResultCallBack callBack) {

        if (!checkContextAndCallBack(context, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(param)) {
            LogUtil.e(TAG, "请求参数 cannot be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "Parameters cannot be empty", null, "");
            return;
        }

        if (!isCanLoading()) {
            LogUtil.e(TAG, "Please do not repeat submit");
            return;
        }
        TransferMain.getInstance().callBack = callBack;
        TransferMain.getInstance().queryQRTransferTarget(context, param);
    }

    /**
     * 查询转账进度
     *
     * @param context
     * @param id       转账Token
     * @param sid
     * @param callBack
     */
    public static void queryTransferProgress(Context context, String id, String sid, TransferResultCallBack callBack) {

        if (!checkContextAndCallBack(context, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(id)) {
            LogUtil.e(TAG, "转账Token cannot be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "Parameters cannot be empty", null, id);
            return;
        }
        if (!checkSID(sid, callBack)) {
            return;
        }
        if (!isCanLoading()) {
            LogUtil.e(TAG, "Please do not repeat submit");
            return;
        }
        LogUtil.e(TAG, "查询转账进度, id = " + id);
        TransferMain.getInstance().callBack = callBack;
        TransferMain.getInstance().queryTransferProgress(context, id, sid);
    }

    /**
     * 查询最近转账联系人
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public static void queryRecentTransContact(Context context, String sid, TransferResultCallBack callBack) {

        if (!checkContextAndCallBack(context, callBack)) {
            return;
        }
        if (!checkSID(sid, callBack)) {
            return;
        }
        if (!isCanLoading()) {
            LogUtil.e(TAG, "Please do not repeat submit");
            return;
        }
        TransferMain.getInstance().queryRecentTransContact(context, sid, callBack);
    }


    /**
     * 检查context 和 callback
     *
     * @param context
     * @param callBack
     * @return
     */
    private static boolean checkContextAndCallBack(Context context, TransferResultCallBack callBack) {

        if (context == null) {
            LogUtil.e(TAG, "context can't be empty");
            return false;
        }
        if (callBack == null) {
            LogUtil.e(TAG, "callback can't be empty");
            return false;
        }
        return true;
    }

    private static boolean checkSID(String sid, TransferResultCallBack callBack) {
        if (TextUtils.isEmpty(sid)) {
            LogUtil.e(TAG, "sid cannot be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "sid cannot be empty", null, "");
            return false;
        }
        return true;
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

}

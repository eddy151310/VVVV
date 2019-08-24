package com.ahdi.wallet.module.payment.withdraw;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.module.payment.withdraw.callback.WithDrawResultCallBack;
import com.ahdi.wallet.module.payment.withdraw.main.WithDrawMain;

/**
 * @author admin
 * 提现sdk入口
 */
public class WithDrawSdk {

    private static final String TAG = WithDrawSdk.class.getSimpleName();

    /**
     * 上次点击时间
     */
    private static long lastClickTime;

    public static final String LOCAL_PAY_SUCCESS = Constants.RET_CODE_SUCCESS;   // 支付完成

    public static final String LOCAL_PAY_PARAM_ERROR = "-3";                     // 参数错误

    public static final String LOCAL_PAY_USER_CANCEL = "-4";                     // 用户取消, 关闭收银台

    public static final String LOCAL_PAY_NETWORK_EXCEPTION = Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION;               // 网络异常

    public static final String LOCAL_PAY_SYSTEM_EXCEPTION = "-6";                // 系统响应解析异常

    public static final String LOCAL_PAY_QUERY_CANCEL = "-7";                    // 查询结果后取消 继续查询

    /**
     * 查询提现信息、提现账户列表 （可以理解为 提现批价）
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public static void queryWDBankCardInfo(Context context, String sid, WithDrawResultCallBack callBack) {
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
        LogUtil.d(TAG, "查询提现信息、提现账户列表");
        WithDrawMain.getInstance().callBack = callBack;
        WithDrawMain.getInstance().queryWDBankCardInfo(context, sid);
    }

    /**
     * 发起提现（提现下单）
     *
     * @param context
     * @param sid
     * @param target   必须   提现目标（银行账户Token）必须
     * @param want     必须   提现金额类型:0支付,1到账金额
     * @param amount   必须   提现金额
     * @param remark   可选   提现备注
     * @param callBack
     */
    public static void createWD(Context context, String sid, String target, int want, String amount, String remark, WithDrawResultCallBack callBack) {
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
        if (TextUtils.isEmpty(target)) {
            LogUtil.d(TAG, "target cannot be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "target cannot be empty", null, "");
            return;
        }
        if (TextUtils.isEmpty(amount)) {
            LogUtil.d(TAG, "amount cannot be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "amount cannot be empty", null, "");
            return;
        }
        WithDrawMain.getInstance().callBack = callBack;
        WithDrawMain.getInstance().createWD(context, sid, target, want, amount, remark);
    }

    /**
     * 查询提现进度
     *
     * @param context
     * @param sid
     * @param ID       必须  提现 token
     * @param callBack
     */
    public static void queryWDProgress(Context context, String sid, String ID, WithDrawResultCallBack callBack) {
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
        if (TextUtils.isEmpty(ID)) {
            LogUtil.d(TAG, "ID cannot be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "ID cannot be empty", null, "");
            return;
        }
        WithDrawMain.getInstance().callBack = callBack;
        WithDrawMain.getInstance().queryWDProgress(context, sid, ID);
    }

    /**
     * 检查context 和 callback
     *
     * @param context
     * @param callBack
     * @return
     */
    private static boolean checkContextAndCallBack(Context context, WithDrawResultCallBack callBack) {

        if (context == null) {
            LogUtil.d(TAG, "context can't be empty");
            return false;
        }
        if (callBack == null) {
            LogUtil.d(TAG, "callback can't be empty");
            return false;
        }
        return true;
    }

    private static boolean checkSID(String sid, WithDrawResultCallBack callBack) {
        if (TextUtils.isEmpty(sid)) {
            LogUtil.d(TAG, "sid cannot be empty");
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

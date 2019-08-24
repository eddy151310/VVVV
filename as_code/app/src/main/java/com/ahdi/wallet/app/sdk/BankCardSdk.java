package com.ahdi.wallet.app.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.callback.BankCardSdkCallBack;
import com.ahdi.wallet.app.main.BankCardSdkMain;
import com.ahdi.wallet.app.response.IsCanBindRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;

import org.json.JSONObject;


public class BankCardSdk {

    public static final String TAG = BankCardSdk.class.getSimpleName();

    public static final String LOCAL_PAY_SUCCESS = Constants.RET_CODE_SUCCESS;   // 支付完成

    public static final String LOCAL_PAY_PARAM_ERROR = "-3";                     // 参数错误

    public static final String LOCAL_PAY_USER_CANCEL = "-4";                     // 用户取消, 关闭收银台

    public static final String LOCAL_PAY_NETWORK_EXCEPTION = Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION;               // 网络异常

    public static final String LOCAL_PAY_SYSTEM_EXCEPTION = "-6";                // 系统响应解析异常

    public static final String LOCAL_PAY_QUERY_CANCEL = "-7";                    // 查询结果后取消 继续查询
    public static final String LOCAL_PAY_UP_CANCEL = "-8";                    // BCA绑卡支付上报取消


    private static boolean checkContextAndCallback(Context context, BankCardSdkCallBack callBack) {
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

    private static boolean checkSid(String sid, BankCardSdkCallBack callBack) {
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
    public static void queryBindCardInfo(Context context, String sid, BankCardSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        BankCardSdkMain.getInstance().queryBindCardInfo(context, sid, callBack);
    }

    /**
     * 申请绑卡
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public static void bindCard(Activity context, String sid, BankCardSdkCallBack callBack) {
        if (TextUtils.isEmpty(sid)) {
            LogUtil.d(TAG, "sid is empty");
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        LoadingDialog loadingDialog = LoadingDialog.showDialogLoading(context, context.getString(R.string.DialogTitle_C0));
        //先判断是否可以绑卡，再去绑卡
        BankCardSdkMain.getInstance().isCanBind(context, sid, new BankCardSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                IsCanBindRsp resp = IsCanBindRsp.decodeJson(IsCanBindRsp.class, jsonObject);
                loadingDialog.dismiss();
                if (TextUtils.equals(code, Constants.RET_CODE_SUCCESS)) {
                    BankCardSdkMain.getInstance().callBack = callBack;
                    BankCardSdkMain.getInstance().bindCardCheckPayPwd(context, sid);
                } else if (resp != null && TextUtils.equals(code, Constants.RET_CODE_A010)) {
                    showErrorDialog(context, errorMsg);
                } else {
                    ToastUtil.showToastShort(context, errorMsg);
                }
            }
        });
    }

    /**
     * 解除绑卡
     *
     * @param context
     * @param BID
     * @param from
     * @param callBack
     */
    public static void unBindCard(Context context, String sid, String BID, int from, BankCardSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(BID)) {
            LogUtil.d(TAG, "BID can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "Params can't be empty", null);
            return;
        }
        BankCardSdkMain.getInstance().callBack = callBack;
        BankCardSdkMain.getInstance().unBindCard(context, sid, BID, from);
    }

    /**
     * 设置绑卡限额
     *
     * @param context
     * @param sid
     * @param bid
     * @param callBack
     */
    public static void setBindLimit(Context context, String sid, String bid, BankCardSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(bid)) {
            LogUtil.d(TAG, "BID can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "Params can't be empty", null);
            return;
        }
        BankCardSdkMain.getInstance().callBack = callBack;
        BankCardSdkMain.getInstance().setBindLimit(context, sid, bid);
    }

    private static void showErrorDialog(Activity activity, String errorMsg) {
        new CommonDialog
                .Builder(activity)
                .setMessage(errorMsg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(activity.getString(com.ahdi.lib.utils.R.string.DialogButton_B0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public static void onDestroy() {
        BankCardSdkMain.getInstance().onDestroy();
    }
}
package com.ahdi.wallet.module.QRCode;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.R;
import com.ahdi.wallet.module.QRCode.bean.PayCodePINConfirmBean;
import com.ahdi.wallet.module.QRCode.bean.PayCodeInitBean;
import com.ahdi.wallet.module.QRCode.callback.QRCodeSdkCallBack;
import com.ahdi.wallet.module.QRCode.main.QRCodeMain;

import java.util.ArrayList;

/**
 * Date: 2019/6/24 下午5:14
 * Author: kay lau
 * Description:
 */
public class QRCodeSdk {

    private static final String TAG = QRCodeSdk.class.getSimpleName();


    public static final String LOCAL_PAY_SUCCESS = Constants.RET_CODE_SUCCESS;   // 成功

    public static final String LOCAL_PAY_PARAM_ERROR = "-3";                     // 参数错误

    public static final String LOCAL_PAY_USER_CANCEL = "-4";                     // 用户取消, 关闭收银台

    public static final String LOCAL_PAY_NETWORK_EXCEPTION = Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION;               // 网络异常

    public static final String LOCAL_PAY_SYSTEM_EXCEPTION = "-6";                // 系统响应解析异常

    public static final String LOCAL_PAY_QUERY_CANCEL = "-7";

    /**
     * 是否可以再次加载 防止多次调用接口
     *
     * @return
     */

    private static boolean checkContextAndCallback(Context context, QRCodeSdkCallBack callBack) {
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

    private static boolean checkSid(String sid, QRCodeSdkCallBack callBack) {
        if (TextUtils.isEmpty(sid)) {
            LogUtil.d(TAG, "sid can't be empty");
            callBack.onResult(LOCAL_PAY_PARAM_ERROR, "sid can't be empty", null);
            return false;
        }
        return true;
    }

    /**
     * pay码 初始化
     *
     * @param context
     * @param bean
     * @param sid
     * @param callBack
     */
    public static void initPayCode(Context context, PayCodeInitBean bean, String sid, QRCodeSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }

        if (bean == null) {
            LogUtil.d(TAG, "PayCodeInitBean can't be empty");
        }

        if (TextUtils.isEmpty(bean.getPrivateKey()) || TextUtils.isEmpty(bean.getPublicKey())) {
            callBack.onResult(Constants.LOCAL_RSA_CREATE_FAIL, context.getString(R.string.Toast_B0), null);
        }
        QRCodeMain.getInstance().initPayCode(context, bean, sid, callBack);
    }

    /**
     * pay码 是否使用查询
     *
     * @param context
     * @param clientId
     * @param timesLot
     * @param sid
     * @param callBack
     */
    public static void payCodeUseQuery(Context context, long clientId, ArrayList<String> timesLot, String sid, QRCodeSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        QRCodeMain.getInstance().payAuthCodeUseQuery(context, clientId, timesLot, sid, callBack);
    }

    /**
     * pay码 支付结果查询
     *
     * @param context
     * @param tt
     * @param ot
     * @param sid
     * @param callBack
     */
    public static void queryPayCodePayResult(Context context, String tt, String ot, String sid, QRCodeSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
//        AccountSdkMain.getInstance().payAuthCodeUseQuery(context, clientId, timesLot, sid, callBack);
    }

    /**
     * pay码 支付密码确认
     *
     * @param context
     * @param bean
     * @param payAuthType
     * @param sid
     * @param callBack
     */
    public static void payCodePINConfirm(Context context, PayCodePINConfirmBean bean, String payAuthType, String sid, QRCodeSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        QRCodeMain.getInstance().payAuthCodeConfirm(context, bean, sid, payAuthType, callBack);
    }

    /**
     * 收款二维码
     *
     * @param context
     * @param amount
     * @param sid
     * @param callBack
     */
    public static void collectQR(Context context, String amount, String sid, QRCodeSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        QRCodeMain.getInstance().callBack = callBack;
        QRCodeMain.getInstance().collectQR(context, amount, sid);
    }
}

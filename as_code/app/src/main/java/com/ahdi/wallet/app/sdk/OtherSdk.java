package com.ahdi.wallet.app.sdk;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.wallet.app.callback.OtherSdkCallBack;
import com.ahdi.wallet.app.main.OtherSdkMain;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;

/**
 * @author zhaohe
 * 配置文件等
 */
public class OtherSdk {

    public static final String TAG = OtherSdk.class.getSimpleName();

    public static final String LOCAL_SUCCESS = Constants.RET_CODE_SUCCESS;   // 成功

    public static final String LOCAL_PARAM_ERROR = "-3";                     // 参数错误

    public static final String LOCAL_USER_CANCEL = "-4";                     // 用户取消

    public static final String LOCAL_NETWORK_EXCEPTION = Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION;               // 网络异常

    public static final String LOCAL_SYSTEM_EXCEPTION = "-6";                // 系统响应解析异常

    /**
     * VA充值引导类型 mbank:   Mbank银行充值引导。
     */
    public static final String BANK_TYPE_MBANK = "mbank";

    /**
     * VA充值引导类型other:  其它银行充值引导
     */
    public static final String BANK_TYPE_OTHER = "other";

    private static boolean checkContextAndCallback(Context context, OtherSdkCallBack callBack) {
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

    private static boolean checkSid(String sid, OtherSdkCallBack callBack) {
        if (TextUtils.isEmpty(sid)) {
            LogUtil.d(TAG, "sid can't be empty");
            callBack.onResult(LOCAL_PARAM_ERROR, "sid can't be empty", null);
            return false;
        }
        return true;
    }

    /**
     * 获取配置
     *
     * @param context
     * @param sid
     * @param cfgVer
     * @param callBack
     */
    public static void getConfig(Context context, String sid, String cfgVer, OtherSdkCallBack callBack) {
        OtherSdkMain.getInstance().getConfig(context, sid, cfgVer, callBack);
    }


    /**
     * 获取配置
     *
     * @param context
     * @param sid
     * @param scanCode
     * @param callBack
     */
    public static void scanCheck(Context context, String sid, String scanCode, OtherSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        OtherSdkMain.getInstance().scanCheck(context, sid, scanCode, callBack);
    }

    /**
     * 用户协议内容加载
     *
     * @param context
     * @param action
     * @param callBack
     */
    public static void getUserAgreement(Context context, String action, OtherSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        OtherSdkMain.getInstance().getUserAgreement(context, action, callBack);
    }

    /**
     * 获取首页banner信息
     *
     * @param context
     * @param param
     * @param callBack
     */
    public static void getBannerInfo(Context context, String param, OtherSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (TextUtils.isEmpty(param)) {
            LogUtil.e(TAG, "请求参数 cannot be empty");
            callBack.onResult(LOCAL_PARAM_ERROR, "Parameters cannot be empty", null);
            return;
        }
        OtherSdkMain.getInstance().getBannerInfo(context, param, callBack);
    }

    /**
     * url
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public static void getBankVaUrl(Context context, String type, String sid, OtherSdkCallBack callBack) {
        if (checkContextAndCallback(context, callBack)) {
            return;
        }
        if (!checkSid(sid, callBack)) {
            return;
        }
        OtherSdkMain.getInstance().getBankVaUrl(context, type, sid, callBack);
    }
}
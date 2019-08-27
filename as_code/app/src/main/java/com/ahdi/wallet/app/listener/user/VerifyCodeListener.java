package com.ahdi.wallet.app.listener.user;

import android.text.TextUtils;

import com.ahdi.wallet.app.response.aaa.SmsCodeRsp;
import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.wallet.app.main.UserSdkMain;
import com.ahdi.wallet.app.response.VerifyCodeRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * Date: 2018/4/25 下午6:03
 * Author: kay lau
 * Description:
 */
public class VerifyCodeListener implements HttpReqTaskListener {

    public static final String TAG = VerifyCodeListener.class.getSimpleName();

    private UserSdkCallBack callBack;

    public VerifyCodeListener(UserSdkCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, "doInBackground响应数据: " + json.toString());
        }
        SmsCodeRsp resp = SmsCodeRsp.decodeJson(SmsCodeRsp.class, json);
        if (callBack == null) {
            LogUtil.e(TAG, "VerifyCodeListener bankCardCallBack = null");
            return;
        }
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().retCode, UserSdk.LOCAL_PAY_SUCCESS)) {
                callBack.onResult(UserSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().retMsg, json);
            } else {
                callBack.onResult(resp.getmHeader().retCode, resp.getmHeader().retMsg, json);
            }
        } else {
            callBack.onResult(UserSdk.LOCAL_PAY_SYSTEM_EXCEPTION, UserSdkMain.getInstance().default_error, null);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }

    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        if (callBack != null) {
            String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
            callBack.onResult(UserSdk.LOCAL_PAY_NETWORK_EXCEPTION, "", json);
        }
    }
}

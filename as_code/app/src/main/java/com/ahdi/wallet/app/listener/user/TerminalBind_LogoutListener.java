package com.ahdi.wallet.app.listener.user;

import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.wallet.app.main.UserSdkMain;
import com.ahdi.wallet.app.response.TerminalBind_LogoutRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * Date: 2018/1/5 下午5:09
 * Author: kay lau
 * Description:
 */
public class TerminalBind_LogoutListener implements HttpReqTaskListener {

    private static final String TAG = TerminalBind_LogoutListener.class.getSimpleName();

    private UserSdkCallBack callBack;

    public TerminalBind_LogoutListener(UserSdkCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        if (callBack == null) {
            LogUtil.e(TAG, "TerminalBind_LogoutListener bankCardCallBack = null");
            return;
        }
        TerminalBind_LogoutRsp resp = TerminalBind_LogoutRsp.decodeJson(TerminalBind_LogoutRsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().RetCode, UserSdk.LOCAL_PAY_SUCCESS)) {
                callBack.onResult(UserSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().ErrMsg, json);
            } else {
                callBack.onResult(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, json);
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
            callBack.onResult(UserSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json);
        }
    }
}

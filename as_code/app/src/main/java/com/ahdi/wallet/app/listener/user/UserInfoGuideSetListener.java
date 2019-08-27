package com.ahdi.wallet.app.listener.user;

import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.wallet.app.main.UserSdkMain;
import com.ahdi.wallet.app.response.UserInfoGuideSetRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * Date: 2018/7/2 下午3:46
 * Author: kay lau
 * Description:
 */
public class UserInfoGuideSetListener implements HttpReqTaskListener {

    private static final String TAG = UserInfoGuideSetListener.class.getSimpleName();

    private UserSdkCallBack callBack;

    public UserInfoGuideSetListener(UserSdkCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, TAG, json.toString());
        }
        if (callBack == null) {
            LogUtil.e(TAG, TAG + "bankCardCallBack = null");
            return;
        }
        UserInfoGuideSetRsp resp = UserInfoGuideSetRsp.decodeJson(UserInfoGuideSetRsp.class, json);

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
            callBack.onResult(UserSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json);
        } else {
            LogUtil.e(TAG, TAG + "bankCardCallBack = null");
        }
    }
}

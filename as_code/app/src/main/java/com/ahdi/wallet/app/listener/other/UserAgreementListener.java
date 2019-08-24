package com.ahdi.wallet.app.listener.other;

import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.callback.OtherSdkCallBack;
import com.ahdi.wallet.app.main.UserSdkMain;
import com.ahdi.wallet.app.response.UserAgreementRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * Date: 2018/6/29 下午2:25
 * Author: kay lau
 * Description:
 */
public class UserAgreementListener implements HttpReqTaskListener {

    private static final String TAG = UserAgreementListener.class.getSimpleName();

    private OtherSdkCallBack callBack;

    public UserAgreementListener(OtherSdkCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, "UserAgreementListener响应数据: " + json.toString());
        }
        if (callBack == null) {
            return;
        }
        UserAgreementRsp resp = UserAgreementRsp.decodeJson(UserAgreementRsp.class, json);
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

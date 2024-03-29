package com.ahdi.wallet.app.listener.user;

import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.main.UserSdkMain;
import com.ahdi.wallet.app.response.RegisterRsp;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * Date: 2018/1/5 下午5:09
 * Author: kay lau
 * Description:
 */
public class RegisterListener implements HttpReqTaskListener {

    private static final String TAG = RegisterListener.class.getSimpleName();

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, "doInBackground响应数据: " + json.toString());
        }
        RegisterRsp resp = RegisterRsp.decodeJson(RegisterRsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().retCode, UserSdk.LOCAL_PAY_SUCCESS)) {
                UserSdkMain.getInstance().onResultBack(UserSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().retMsg, json);
            } else {
                UserSdkMain.getInstance().onResultBack(resp.getmHeader().retCode, resp.getmHeader().retMsg, json);
            }
        } else {
            UserSdkMain.getInstance().onResultBack(UserSdk.LOCAL_PAY_SYSTEM_EXCEPTION, UserSdkMain.getInstance().default_error, null);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        UserSdkMain.getInstance().onResultBack(UserSdk.LOCAL_PAY_NETWORK_EXCEPTION, "", json);
    }
}

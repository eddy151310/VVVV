package com.ahdi.wallet.app.listener.other;

import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.OtherSdk;
import com.ahdi.wallet.app.callback.OtherSdkCallBack;
import com.ahdi.wallet.app.main.UserSdkMain;
import com.ahdi.wallet.app.response.GetBankVARsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * @author zhaohe
 */
public class GetBankVAListener implements HttpReqTaskListener {

    private static final String TAG = GetBankVAListener.class.getSimpleName();

    private OtherSdkCallBack callBack;

    public GetBankVAListener(OtherSdkCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, "GetBankVAListener响应数据: " + json.toString());
        }
        if (callBack == null) {
            return;
        }
        GetBankVARsp resp = GetBankVARsp.decodeJson(GetBankVARsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().retCode, OtherSdk.LOCAL_SUCCESS)) {
                callBack.onResult(OtherSdk.LOCAL_SUCCESS, resp.getmHeader().retMsg, json);
            } else {
                callBack.onResult(resp.getmHeader().retCode, resp.getmHeader().retMsg, json);
            }
        } else {
            callBack.onResult(OtherSdk.LOCAL_SYSTEM_EXCEPTION, UserSdkMain.getInstance().default_error, null);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        if (callBack != null) {
            String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
            callBack.onResult(OtherSdk.LOCAL_NETWORK_EXCEPTION, errorMsg, json);
        }
    }
}

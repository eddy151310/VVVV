package com.ahdi.wallet.app.listener.other;

import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.callback.OtherSdkCallBack;
import com.ahdi.wallet.app.main.UserSdkMain;
import com.ahdi.wallet.app.response.BannerInfoRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * @author zhaohe
 */
public class BannerInfoListener implements HttpReqTaskListener {

    private static final String TAG = BannerInfoListener.class.getSimpleName();

    private OtherSdkCallBack callBack;

    public BannerInfoListener(OtherSdkCallBack callBack) {
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
            return;
        }
        BannerInfoRsp resp = BannerInfoRsp.decodeJson(BannerInfoRsp.class, json);
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

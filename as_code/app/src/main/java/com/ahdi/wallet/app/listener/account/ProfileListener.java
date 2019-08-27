package com.ahdi.wallet.app.listener.account;

import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.AccountSdk;
import com.ahdi.wallet.app.callback.AccountSdkCallBack;
import com.ahdi.wallet.cashier.main.PayCashierMain;
import com.ahdi.wallet.app.response.ProfileRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * @author admin
 * 用户消息概要
 */
public class ProfileListener implements HttpReqTaskListener {

    private static final String TAG = ProfileListener.class.getSimpleName();

    private AccountSdkCallBack callBack;

    public ProfileListener(AccountSdkCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, "doInBackground响应数据: " + json.toString());
        }
        ProfileRsp resp = ProfileRsp.decodeJson(ProfileRsp.class, json);
        if (callBack == null) {
            LogUtil.e(TAG, TAG + " onPostExecute: bankCardCallBack = null");
            return;
        }
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().retCode, AccountSdk.LOCAL_PAY_SUCCESS)) {
                callBack.onResult(AccountSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().retMsg, json);
            } else {
                callBack.onResult(resp.getmHeader().retCode, resp.getmHeader().retMsg, json);
            }
        } else {
            callBack.onResult(AccountSdk.LOCAL_PAY_SYSTEM_EXCEPTION, PayCashierMain.getInstance().default_error, null);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? "MsgDetailListener--onError(JSONObject e)" : json.toString());
        if (callBack != null) {
            String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
            callBack.onResult(AccountSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json);
        } else {
            LogUtil.e(TAG, TAG + " onError: bankCardCallBack = null");
        }
    }
}

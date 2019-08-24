package com.ahdi.wallet.app.listener.account;

import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.AccountSdk;
import com.ahdi.wallet.app.callback.AccountSdkCallBack;
import com.ahdi.wallet.cashier.main.PayCashierMain;
import com.ahdi.wallet.app.response.TransListRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;


public class TransListListener implements HttpReqTaskListener {

    private static final String TAG = TransListListener.class.getSimpleName();

    private AccountSdkCallBack callBack;

    public TransListListener(AccountSdkCallBack callBack) {
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
        TransListRsp resp = TransListRsp.decodeJson(TransListRsp.class, json);
        if (callBack == null) {
            LogUtil.e(TAG, TAG + " onPostExecute: bankCardCallBack = null");
            return;
        }
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().RetCode, AccountSdk.LOCAL_PAY_SUCCESS)) {
                callBack.onResult(AccountSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().ErrMsg, json);
            } else {
                callBack.onResult(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, json);
            }
        } else {
            callBack.onResult(AccountSdk.LOCAL_PAY_SYSTEM_EXCEPTION, PayCashierMain.getInstance().default_error, null);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? "TransListListener--onError(JSONObject e)" : json.toString());
        if (callBack != null) {
            String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
            callBack.onResult(AccountSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json);
        } else {
            LogUtil.e(TAG, TAG + " onError: bankCardCallBack = null");
        }
    }
}

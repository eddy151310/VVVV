package com.ahdi.wallet.app.listener.account;

import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.BankCardSdk;
import com.ahdi.wallet.app.callback.BankCardSdkCallBack;
import com.ahdi.wallet.cashier.main.PayCashierMain;
import com.ahdi.wallet.app.response.SelectCardTypeRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

public class SelectCardTypeListener implements HttpReqTaskListener {

    private static final String TAG = SelectCardTypeListener.class.getSimpleName();

    private BankCardSdkCallBack callBack;

    public SelectCardTypeListener(BankCardSdkCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        if (callBack == null) {
            LogUtil.e(TAG, TAG + " onPostExecute: bankCardCallBack = null");
            return;
        }
        SelectCardTypeRsp resp = SelectCardTypeRsp.decodeJson(SelectCardTypeRsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().retCode, BankCardSdk.LOCAL_PAY_SUCCESS)) {
                callBack.onResult(BankCardSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().retMsg, json);
            } else {
                callBack.onResult(resp.getmHeader().retCode, resp.getmHeader().retMsg, json);
            }
        } else {
            callBack.onResult(BankCardSdk.LOCAL_PAY_SYSTEM_EXCEPTION, PayCashierMain.getInstance().default_error, null);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        if (callBack != null) {
            String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
            callBack.onResult(BankCardSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json);
        }
    }
}

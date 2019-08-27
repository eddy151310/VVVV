package com.ahdi.wallet.app.listener.account;

import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.BankAccountSdk;
import com.ahdi.wallet.app.callback.BankAccountSdkCallBack;
import com.ahdi.wallet.cashier.main.PayCashierMain;
import com.ahdi.wallet.app.response.QueryBankAccountInfoRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;


public class QueryBindAccountInfoListener implements HttpReqTaskListener {

    private static final String TAG = QueryBindAccountInfoListener.class.getSimpleName();

    private BankAccountSdkCallBack callBack;

    public QueryBindAccountInfoListener(BankAccountSdkCallBack callBack) {
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
        QueryBankAccountInfoRsp resp = QueryBankAccountInfoRsp.decodeJson(QueryBankAccountInfoRsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().retCode, BankAccountSdk.LOCAL_PAY_SUCCESS)) {
                callBack.onResult(BankAccountSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().retMsg, json);
            } else {
                callBack.onResult(resp.getmHeader().retCode, resp.getmHeader().retMsg, json);
            }
        } else {
            callBack.onResult(BankAccountSdk.LOCAL_PAY_SYSTEM_EXCEPTION, PayCashierMain.getInstance().default_error, null);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        if (callBack != null) {
            String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
            callBack.onResult(BankAccountSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json);
        }
    }
}

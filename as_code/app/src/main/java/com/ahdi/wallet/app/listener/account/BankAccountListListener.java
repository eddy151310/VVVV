package com.ahdi.wallet.app.listener.account;

import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.BankAccountSdk;
import com.ahdi.wallet.app.main.BankAccountSdkMain;
import com.ahdi.wallet.app.response.BankAccountListRsp;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

public class BankAccountListListener implements HttpReqTaskListener {

    private static final String TAG = BankAccountListListener.class.getSimpleName();

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        BankAccountListRsp resp = BankAccountListRsp.decodeJson(BankAccountListRsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().retCode, BankAccountSdk.LOCAL_PAY_SUCCESS)) {
                BankAccountSdkMain.getInstance().onResultBack(BankAccountSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().retMsg, json);
            } else {
                BankAccountSdkMain.getInstance().onResultBack(resp.getmHeader().retCode, resp.getmHeader().retMsg, json);
            }
        } else {
            BankAccountSdkMain.getInstance().onResultBack(BankAccountSdk.LOCAL_PAY_SYSTEM_EXCEPTION, BankAccountSdkMain.getInstance().default_error, null);
            LogUtil.d(TAG, "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        BankAccountSdkMain.getInstance().onResultBack(BankAccountSdk.LOCAL_PAY_NETWORK_EXCEPTION, "", json);
    }
}

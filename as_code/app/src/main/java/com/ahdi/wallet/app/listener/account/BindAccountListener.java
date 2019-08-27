package com.ahdi.wallet.app.listener.account;

import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.BankAccountSdk;
import com.ahdi.wallet.app.callback.BankAccountSdkCallBack;
import com.ahdi.wallet.app.main.BankAccountSdkMain;
import com.ahdi.wallet.app.response.BindAccountRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * Date: 2018/1/5 下午5:09
 * Author: kay lau
 * Description:
 */
public class BindAccountListener implements HttpReqTaskListener {

    private static final String TAG = BindAccountListener.class.getSimpleName();

    private BankAccountSdkCallBack callBack;

    public BindAccountListener(BankAccountSdkCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        if (callBack == null) {
            LogUtil.e(TAG, " onPostExecute: bankCardCallBack = null");
            return;
        }
        BindAccountRsp resp = BindAccountRsp.decodeJson(BindAccountRsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().retCode, BankAccountSdk.LOCAL_PAY_SUCCESS)) {
                callBack.onResult(BankAccountSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().retMsg, json);
            } else {
                callBack.onResult(resp.getmHeader().retCode, resp.getmHeader().retMsg, json);
            }
        } else {
            callBack.onResult(BankAccountSdk.LOCAL_PAY_SYSTEM_EXCEPTION, BankAccountSdkMain.getInstance().default_error, null);
            LogUtil.d(TAG, "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? "onError(JSONObject e)" : json.toString());
        if (callBack != null) {
            String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
            callBack.onResult(BankAccountSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json);
        }
    }
}

package com.ahdi.wallet.app.listener.account;

import com.ahdi.wallet.app.sdk.BankCardSdk;
import com.ahdi.wallet.app.callback.BankCardSdkCallBack;
import com.ahdi.wallet.app.main.BankCardSdkMain;
import com.ahdi.wallet.app.response.IsCanBindRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

/**
 * 是否可以绑卡
 */
public class IsCanBindCardListener implements HttpReqTaskListener {

    private static final String TAG = IsCanBindCardListener.class.getSimpleName();

    private BankCardSdkCallBack cardSdkCallBack;

    public IsCanBindCardListener(BankCardSdkCallBack cardSdkCallBack) {
        this.cardSdkCallBack = cardSdkCallBack;
    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        IsCanBindRsp resp = IsCanBindRsp.decodeJson(IsCanBindRsp.class, json);
        if (resp != null) {
            if (BankCardSdk.LOCAL_PAY_SUCCESS.equals(resp.getmHeader().retCode)) {
                cardSdkCallBack.onResult(BankCardSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().retMsg, json);
            } else {
                cardSdkCallBack.onResult(BankCardSdk.LOCAL_PAY_SYSTEM_EXCEPTION, resp.getmHeader().retMsg, json);
            }
        } else {
            cardSdkCallBack.onResult(BankCardSdk.LOCAL_PAY_SYSTEM_EXCEPTION, BankCardSdkMain.getInstance().default_error, null);
            LogUtil.d(TAG, "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        if (cardSdkCallBack != null) {
            String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
            cardSdkCallBack.onResult(BankCardSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json);
        }
    }
}

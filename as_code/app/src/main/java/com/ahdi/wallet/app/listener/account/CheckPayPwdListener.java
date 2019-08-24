package com.ahdi.wallet.app.listener.account;

import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.AccountSdk;
import com.ahdi.wallet.app.callback.AccountSdkCallBack;
import com.ahdi.wallet.app.main.AccountSdkMain;
import com.ahdi.wallet.app.response.CheckPayPwdRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/3/15.
 */
public class CheckPayPwdListener implements HttpReqTaskListener {

    private static final String TAG = CheckPayPwdListener.class.getSimpleName();

    private AccountSdkCallBack callBack;

    public CheckPayPwdListener(AccountSdkCallBack callBack) {
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
            LogUtil.e(TAG, "CheckPayPwdListener callback == null");
            return;
        }
        CheckPayPwdRsp resp = CheckPayPwdRsp.decodeJson(CheckPayPwdRsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().RetCode, AccountSdk.LOCAL_PAY_SUCCESS)) {
                callBack.onResult(AccountSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().ErrMsg, json);
            } else {
                callBack.onResult(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, null);
            }
        } else {
            callBack.onResult(AccountSdk.LOCAL_PAY_SYSTEM_EXCEPTION, AccountSdkMain.getInstance().default_error, null);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        if (callBack != null) {
            String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
            callBack.onResult(AccountSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json);
        }
    }
}

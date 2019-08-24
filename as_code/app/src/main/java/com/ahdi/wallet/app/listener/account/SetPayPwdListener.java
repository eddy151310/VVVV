package com.ahdi.wallet.app.listener.account;

import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.AccountSdk;
import com.ahdi.wallet.app.main.AccountSdkMain;
import com.ahdi.wallet.app.response.SetPayPwdRsp;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * Date: 2018/1/5 下午5:09
 * Author: kay lau
 * Description:
 */
public class SetPayPwdListener implements HttpReqTaskListener {

    private static final String TAG = SetPayPwdListener.class.getSimpleName();

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        SetPayPwdRsp resp = SetPayPwdRsp.decodeJson(SetPayPwdRsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().RetCode, AccountSdk.LOCAL_PAY_SUCCESS)) {
                AccountSdkMain.getInstance().onResultBack(AccountSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().ErrMsg, json);
            } else {
                AccountSdkMain.getInstance().onResultBack(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, json);
            }
        } else {
            AccountSdkMain.getInstance().onResultBack(AccountSdk.LOCAL_PAY_SYSTEM_EXCEPTION, AccountSdkMain.getInstance().default_error, null);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        AccountSdkMain.getInstance().onResultBack(AccountSdk.LOCAL_PAY_NETWORK_EXCEPTION, "", json);
    }
}

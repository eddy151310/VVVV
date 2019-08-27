package com.ahdi.wallet.app.listener.idverify;

import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.IDVerifySdk;
import com.ahdi.wallet.app.callback.IDVerifyCallBack;
import com.ahdi.wallet.app.main.IDVerifySdkMain;
import com.ahdi.wallet.app.response.RnaTypeRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * 实名认证类型
 *
 * @author zhaohe
 */
public class RnaTypesListener implements HttpReqTaskListener {

    private static final String TAG = RnaTypesListener.class.getSimpleName();

    private IDVerifyCallBack callBack;

    public RnaTypesListener(IDVerifyCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        if (callBack == null) {
            return;
        }
        RnaTypeRsp resp = RnaTypeRsp.decodeJson(RnaTypeRsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().retCode, IDVerifySdk.LOCAL_SUCCESS)) {
                callBack.onResult(resp.getmHeader().retCode, resp.getmHeader().retMsg, json);
            } else {
                callBack.onResult(resp.getmHeader().retCode, resp.getmHeader().retMsg, json);
            }
        } else {
            callBack.onResult(IDVerifySdk.LOCAL_SYSTEM_EXCEPTION, IDVerifySdkMain.getInstance().default_error, null);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? "RnaTypesListener--onError(JSONObject e)" : json.toString());
        if (callBack != null) {
            String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
            callBack.onResult(IDVerifySdk.LOCAL_NETWORK_EXCEPTION, errorMsg, json);
        }
    }
}

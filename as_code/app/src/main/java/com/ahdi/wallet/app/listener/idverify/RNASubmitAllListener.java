package com.ahdi.wallet.app.listener.idverify;

import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.IDVerifySdk;
import com.ahdi.wallet.app.callback.IDVerifyCallBack;
import com.ahdi.wallet.app.main.IDVerifySdkMain;
import com.ahdi.wallet.app.response.RNASubmitAllRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * 实名认证
 *
 * @author zhaohe
 */
public class RNASubmitAllListener implements HttpReqTaskListener {

    private static final String TAG = RNASubmitAllListener.class.getSimpleName();

    private IDVerifyCallBack callBack;

    public RNASubmitAllListener(IDVerifyCallBack callBack) {
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
        if (callBack == null){
            return;
        }
        RNASubmitAllRsp resp = RNASubmitAllRsp.decodeJson(RNASubmitAllRsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().RetCode, IDVerifySdk.LOCAL_SUCCESS)) {
                callBack.onResult(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, json);
            } else {
                callBack.onResult(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, json);
            }
        } else {
            callBack.onResult(IDVerifySdk.LOCAL_SYSTEM_EXCEPTION, IDVerifySdkMain.getInstance().default_error, null);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? "KTPSubmitListener--onError(JSONObject e)" : json.toString());
        if (callBack != null) {
            String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
            callBack.onResult(IDVerifySdk.LOCAL_NETWORK_EXCEPTION, errorMsg, json);
        }
    }
}

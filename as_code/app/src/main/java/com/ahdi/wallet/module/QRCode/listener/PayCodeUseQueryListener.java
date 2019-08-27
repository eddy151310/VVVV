package com.ahdi.wallet.module.QRCode.listener;

import android.text.TextUtils;

import com.ahdi.wallet.module.QRCode.QRCodeSdk;
import com.ahdi.wallet.module.QRCode.callback.QRCodeSdkCallBack;
import com.ahdi.wallet.module.QRCode.main.QRCodeMain;
import com.ahdi.wallet.module.QRCode.response.PayCodeUseQueryRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * Date: 2018/1/5 下午5:09
 * Author: kay lau
 * Description:
 */
public class PayCodeUseQueryListener implements HttpReqTaskListener {

    private static final String TAG = PayCodeUseQueryListener.class.getSimpleName();

    private QRCodeSdkCallBack callBack;

    public PayCodeUseQueryListener(QRCodeSdkCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        PayCodeUseQueryRsp resp = PayCodeUseQueryRsp.decodeJson(PayCodeUseQueryRsp.class, json);
        if (resp != null) {
            if (callBack == null) {
                LogUtil.e(TAG, "callBack is null");
                return;
            }
            if (TextUtils.equals(resp.getmHeader().retCode, QRCodeSdk.LOCAL_PAY_SUCCESS)) {
                callBack.onResult(QRCodeSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().retMsg, json);
            } else {
                callBack.onResult(resp.getmHeader().retCode, resp.getmHeader().retMsg, json);
            }
        } else {
            callBack.onResult(QRCodeSdk.LOCAL_PAY_SYSTEM_EXCEPTION, QRCodeMain.getInstance().default_error, null);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        if (callBack == null) {
            LogUtil.e(TAG, "bankCardCallBack is null");
            return;
        }
        String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
        callBack.onResult(QRCodeSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json);
    }
}
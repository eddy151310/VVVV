package com.ahdi.wallet.module.QRCode.listener;

import android.text.TextUtils;

import com.ahdi.wallet.module.QRCode.QRCodeSdk;
import com.ahdi.wallet.module.QRCode.main.QRCodeMain;
import com.ahdi.wallet.module.QRCode.response.PayCodeInitRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.module.QRCode.callback.QRCodeSdkCallBack;

import org.json.JSONObject;

/**
 * Date: 2018/1/5 下午5:09
 * Author: kay lau
 * Description:
 */
public class PayCodeInitListener implements HttpReqTaskListener {

    private static final String TAG = PayCodeInitListener.class.getSimpleName();

    private QRCodeSdkCallBack callBack;

    public PayCodeInitListener(QRCodeSdkCallBack callBack) {
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
        PayCodeInitRsp resp = PayCodeInitRsp.decodeJson(PayCodeInitRsp.class, json);
        if (resp != null) {
            if (callBack == null) {
                LogUtil.e(TAG, "callback is null");
                return;
            }
            if (TextUtils.equals(resp.getmHeader().RetCode, QRCodeSdk.LOCAL_PAY_SUCCESS)) {
                callBack.onResult(QRCodeSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().ErrMsg, json);
            } else {
                callBack.onResult(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, json);
            }
        } else {
            callBack.onResult(QRCodeSdk.LOCAL_PAY_SYSTEM_EXCEPTION, QRCodeMain.getInstance().default_error, null);
            LogUtil.d(TAG, "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        if (callBack != null) {
            String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
            callBack.onResult(QRCodeSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json);
        }
    }
}

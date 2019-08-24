package com.ahdi.wallet.bca.listener;

import com.ahdi.wallet.bca.BcaSdk;
import com.ahdi.wallet.bca.callback.BcaOTPCallBack;
import com.ahdi.wallet.bca.main.BcaSdkMain;
import com.ahdi.wallet.bca.response.BcaOrderRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

/**
 * @author xiaoniu
 */
public class BcaOrderListener implements HttpReqTaskListener {

    private static final String TAG = BcaOrderListener.class.getSimpleName();

    private BcaOTPCallBack callBack;

    public BcaOrderListener(BcaOTPCallBack callBack) {
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
            LogUtil.e(TAG, "BcaOrderListener  callBack= null");
            return;
        }
        BcaOrderRsp resp = BcaOrderRsp.decodeJson(BcaOrderRsp.class, json);
        if (resp != null) {
            callBack.onResult(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, json);
        } else {
            callBack.onResult(BcaSdk.LOCAL_PAY_SYSTEM_EXCEPTION, BcaSdkMain.getInstance().default_error, null);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        if (callBack != null) {
            String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
            callBack.onResult(BcaSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json);
        } else {
            LogUtil.e(TAG, "BcaOrderListener CallBack = null");
        }
    }
}

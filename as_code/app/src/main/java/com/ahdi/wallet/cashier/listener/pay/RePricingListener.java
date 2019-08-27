package com.ahdi.wallet.cashier.listener.pay;

import com.ahdi.wallet.cashier.PayCashierSdk;
import com.ahdi.wallet.cashier.callback.PaymentSdkCallBack;
import com.ahdi.wallet.cashier.main.PayCashierMain;
import com.ahdi.wallet.cashier.response.pay.PricingResponse;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

/**
 * Date: 2018/1/5 下午5:09
 * Author: kay lau
 * Description:
 */
public class RePricingListener implements HttpReqTaskListener {

    private static final String TAG = RePricingListener.class.getSimpleName();

    private PaymentSdkCallBack callBack;

    public RePricingListener(PaymentSdkCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        if (callBack == null) {
            LogUtil.e(TAG, "RePricingListener  callBack = null");
            return;
        }
        PricingResponse resp = PricingResponse.decodeJson(PricingResponse.class, json);
        if (resp != null) {
            callBack.onResult(resp.getmHeader().retCode, resp.getmHeader().retMsg, json);

        } else {
            callBack.onResult(PayCashierSdk.LOCAL_PAY_SYSTEM_EXCEPTION, PayCashierMain.getInstance().default_error, null);
            LogUtil.d(TAG, "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        if (callBack != null) {
            String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
            callBack.onResult(PayCashierSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json);
        }
    }
}

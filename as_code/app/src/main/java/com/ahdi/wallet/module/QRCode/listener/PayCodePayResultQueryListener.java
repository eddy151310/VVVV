package com.ahdi.wallet.module.QRCode.listener;

import android.text.TextUtils;

import com.ahdi.wallet.cashier.PayCashierSdk;
import com.ahdi.wallet.cashier.main.PayCashierMain;
import com.ahdi.wallet.cashier.response.pay.PayResultQueryResponse;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * Date: 2018/1/5 下午5:09
 * Author: kay lau
 * Description:
 */
public class PayCodePayResultQueryListener implements HttpReqTaskListener {

    private static final String TAG = PayCodePayResultQueryListener.class.getSimpleName();

    public PayCodePayResultQueryListener() {
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        PayResultQueryResponse resp = PayResultQueryResponse.decodeJson(PayResultQueryResponse.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().RetCode, PayCashierSdk.LOCAL_PAY_SUCCESS)) {
                PayCashierMain.getInstance().onResultBack(PayCashierSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().ErrMsg, json);
            } else {
                PayCashierMain.getInstance().onResultBack(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, json);
            }
        } else {
            LogUtil.d(TAG, TAG + "解析之后为空");
            PayCashierMain.getInstance().onResultBack(PayCashierSdk.LOCAL_PAY_SYSTEM_EXCEPTION, resp.getmHeader().ErrMsg, json);
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        PayCashierMain.getInstance().onResultBack(PayCashierSdk.LOCAL_PAY_NETWORK_EXCEPTION, "", json);
    }

}

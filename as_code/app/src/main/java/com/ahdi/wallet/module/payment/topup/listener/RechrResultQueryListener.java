package com.ahdi.wallet.module.payment.topup.listener;

import android.text.TextUtils;

import com.ahdi.wallet.module.payment.topup.TopUpSdk;
import com.ahdi.wallet.module.payment.topup.main.TopUpMain;
import com.ahdi.wallet.module.payment.topup.response.RechrResultQueryRsp;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * 业务查询(对外提供): 查询充值结果
 */
public class RechrResultQueryListener implements HttpReqTaskListener {

    private static final String TAG = RechrResultQueryListener.class.getSimpleName();

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        RechrResultQueryRsp resp = RechrResultQueryRsp.decodeJson(RechrResultQueryRsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().retCode, TopUpSdk.LOCAL_PAY_SUCCESS)) {
                TopUpMain.getInstance().onResultBack(TopUpSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().retMsg, json, "");
            } else {
                TopUpMain.getInstance().onResultBack(resp.getmHeader().retCode, resp.getmHeader().retMsg, json, "");
            }
        } else {
            LogUtil.d(TAG, TAG + "解析之后为空");
            TopUpMain.getInstance().onResultBack(TopUpSdk.LOCAL_PAY_SYSTEM_EXCEPTION, TopUpMain.getInstance().default_error, null, "");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        TopUpMain.getInstance().onResultBack(TopUpSdk.LOCAL_PAY_NETWORK_EXCEPTION, "", json, "");
    }

}

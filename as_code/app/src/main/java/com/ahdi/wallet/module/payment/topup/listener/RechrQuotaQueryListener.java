package com.ahdi.wallet.module.payment.topup.listener;

import android.text.TextUtils;

import com.ahdi.wallet.module.payment.topup.TopUpSdk;
import com.ahdi.wallet.module.payment.topup.main.TopUpMain;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.module.payment.topup.response.RechrQuotaQueryRsp;

import org.json.JSONObject;

/**
 * Date: 2018/1/5 下午5:09
 * Author: kay lau
 * Description:
 */
public class RechrQuotaQueryListener implements HttpReqTaskListener {

    private static final String TAG = RechrQuotaQueryListener.class.getSimpleName();

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        RechrQuotaQueryRsp resp = RechrQuotaQueryRsp.decodeJson(RechrQuotaQueryRsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().RetCode, TopUpSdk.LOCAL_PAY_SUCCESS)) {
                TopUpMain.getInstance().onResultBack(TopUpSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().ErrMsg, json, "");
            } else {
                TopUpMain.getInstance().onResultBack(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, json, "");
            }
        } else {
            TopUpMain.getInstance().onResultBack(TopUpSdk.LOCAL_PAY_SYSTEM_EXCEPTION, TopUpMain.getInstance().default_error, null, "");
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        TopUpMain.getInstance().onResultBack(TopUpSdk.LOCAL_PAY_NETWORK_EXCEPTION, "", json, "");
    }
}

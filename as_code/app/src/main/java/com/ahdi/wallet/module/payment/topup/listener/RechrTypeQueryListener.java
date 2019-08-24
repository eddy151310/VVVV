package com.ahdi.wallet.module.payment.topup.listener;

import android.text.TextUtils;

import com.ahdi.wallet.module.payment.topup.TopUpSdk;
import com.ahdi.wallet.module.payment.topup.main.TopUpMain;
import com.ahdi.wallet.module.payment.topup.response.RechrTypeQueryRsp;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * Date: 2018/1/5 下午5:09
 * Author: kay lau
 * Description: 充值类型查询
 */
public class RechrTypeQueryListener implements HttpReqTaskListener {

    private static final String TAG = RechrTypeQueryListener.class.getSimpleName();

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        RechrTypeQueryRsp resp = RechrTypeQueryRsp.decodeJson(RechrTypeQueryRsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().RetCode, TopUpSdk.LOCAL_PAY_SUCCESS)) {
                TopUpMain.getInstance().onResultBack(TopUpSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().ErrMsg, json, "");
            } else {
                TopUpMain.getInstance().onResultBack(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, json, "");
            }
        } else {
            TopUpMain.getInstance().onResultBack(TopUpSdk.LOCAL_PAY_SYSTEM_EXCEPTION, TopUpMain.getInstance().default_error, null, "");
            LogUtil.e(TAG, "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        TopUpMain.getInstance().onResultBack(TopUpSdk.LOCAL_PAY_NETWORK_EXCEPTION, "", json, "");
    }
}

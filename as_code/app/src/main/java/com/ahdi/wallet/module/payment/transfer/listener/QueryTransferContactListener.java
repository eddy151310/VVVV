package com.ahdi.wallet.module.payment.transfer.listener;

import android.text.TextUtils;

import com.ahdi.wallet.module.payment.transfer.TransferSdk;
import com.ahdi.wallet.module.payment.transfer.callback.TransferResultCallBack;
import com.ahdi.wallet.module.payment.transfer.main.TransferMain;
import com.ahdi.wallet.module.payment.transfer.response.QueryRecentTransContactResp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/7.
 * @email zhao_zhaohe@163.com
 */

public class QueryTransferContactListener implements HttpReqTaskListener {

    private static final String TAG = QueryTransferContactListener.class.getSimpleName();

    private TransferResultCallBack callBack;

    public QueryTransferContactListener(TransferResultCallBack callBack) {
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
            LogUtil.e(TAG, "QueryTransferContactListener bankCardCallBack = null");
            return;
        }
        QueryRecentTransContactResp resp = QueryRecentTransContactResp.decodeJson(QueryRecentTransContactResp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().RetCode, TransferSdk.LOCAL_PAY_SUCCESS)) {
                callBack.onResult(TransferSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().ErrMsg, json, "");
            } else {
                callBack.onResult(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, json, "");
            }
        } else {
            callBack.onResult(TransferSdk.LOCAL_PAY_SYSTEM_EXCEPTION, TransferMain.getInstance().default_error, null, "");
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.d(TAG, json == null ? "QueryTransferProgressListener--onError(JSONObject e)" : json.toString());
        if (callBack != null) {
            String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
            callBack.onResult(TransferSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json, "");
        }
    }

}

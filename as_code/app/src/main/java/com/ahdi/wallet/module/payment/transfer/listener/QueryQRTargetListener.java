package com.ahdi.wallet.module.payment.transfer.listener;

import android.text.TextUtils;

import com.ahdi.wallet.module.payment.transfer.TransferSdk;
import com.ahdi.wallet.module.payment.transfer.main.TransferMain;
import com.ahdi.wallet.module.payment.transfer.response.QueryQRTransferResp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/7.
 * @email zhao_zhaohe@163.com
 */

public class QueryQRTargetListener implements HttpReqTaskListener {

    private static final String TAG = QueryQRTargetListener.class.getSimpleName();

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        QueryQRTransferResp resp = QueryQRTransferResp.decodeJson(QueryQRTransferResp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().retCode, TransferSdk.LOCAL_PAY_SUCCESS)) {
                TransferMain.getInstance().onResultBack(TransferSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().retMsg, json, "");
            } else {
                TransferMain.getInstance().onResultBack(resp.getmHeader().retCode, resp.getmHeader().retMsg, json, "");
            }
        } else {
            TransferMain.getInstance().onResultBack(TransferSdk.LOCAL_PAY_SYSTEM_EXCEPTION, TransferMain.getInstance().default_error, null, "");
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.d(TAG, json == null ? "QueryTargetListener--onError(JSONObject e)" : json.toString());
        String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
        TransferMain.getInstance().onResultBack(TransferSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json, "");
    }

}

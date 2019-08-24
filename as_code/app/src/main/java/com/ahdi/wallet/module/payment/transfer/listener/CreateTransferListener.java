package com.ahdi.wallet.module.payment.transfer.listener;

import android.text.TextUtils;

import com.ahdi.wallet.module.payment.transfer.TransferSdk;
import com.ahdi.wallet.module.payment.transfer.main.TransferMain;
import com.ahdi.wallet.module.payment.transfer.response.CreateTransferResp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/7.
 * @email zhao_zhaohe@163.com
 * <p>
 * 创建转账的响应回调
 */

public class CreateTransferListener implements HttpReqTaskListener {

    private static final String TAG = CreateTransferListener.class.getSimpleName();
    private String sid;

    public CreateTransferListener(String sid) {
        this.sid = sid;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e("CreateTransferResp", json.toString());
        }
        CreateTransferResp resp = CreateTransferResp.decodeJson(CreateTransferResp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().RetCode, TransferSdk.LOCAL_PAY_SUCCESS)) {
                TransferMain.getInstance().pay(resp.getId(), resp.getPay(), sid, Constants.LOCAL_FROM_PAY);
            } else {
                TransferMain.getInstance().onResultBack(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, json, resp.getId());
            }
        } else {
            TransferMain.getInstance().onResultBack(TransferSdk.LOCAL_PAY_SYSTEM_EXCEPTION, TransferMain.getInstance().default_error, null, "");
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.d(TAG, json == null ? "CreateTransferListener--onError(JSONObject e)" : json.toString());
        String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
        TransferMain.getInstance().onResultBack(TransferSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json, "");
    }

}

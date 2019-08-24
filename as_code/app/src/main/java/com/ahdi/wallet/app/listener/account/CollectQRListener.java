package com.ahdi.wallet.app.listener.account;

import android.text.TextUtils;

import com.ahdi.wallet.module.QRCode.QRCodeSdk;
import com.ahdi.wallet.module.QRCode.main.QRCodeMain;
import com.ahdi.wallet.module.QRCode.response.CollectQRCodeRsp;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * Date: 2018/1/5 下午5:09
 * Author: kay lau
 * Description:
 */
public class CollectQRListener implements HttpReqTaskListener {

    private static final String TAG = CollectQRListener.class.getSimpleName();

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        CollectQRCodeRsp resp = CollectQRCodeRsp.decodeJson(CollectQRCodeRsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().RetCode, QRCodeSdk.LOCAL_PAY_SUCCESS)) {
                QRCodeMain.getInstance().onResultBack(QRCodeSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().ErrMsg, json);
            } else {
                QRCodeMain.getInstance().onResultBack(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, json);
            }
        } else {
            QRCodeMain.getInstance().onResultBack(QRCodeSdk.LOCAL_PAY_SYSTEM_EXCEPTION, QRCodeMain.getInstance().default_error, null);
            LogUtil.d(TAG, "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        QRCodeMain.getInstance().onResultBack(QRCodeSdk.LOCAL_PAY_NETWORK_EXCEPTION, "", json);
    }
}

package com.ahdi.wallet.app.listener.idverify;

import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.IDVerifySdk;
import com.ahdi.wallet.app.main.IDVerifySdkMain;
import com.ahdi.wallet.app.response.AuditQRRsp;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * 实名认证审核结果查询
 *
 * @author zhaohe
 */
public class AuditQRListener implements HttpReqTaskListener {

    private static final String TAG = AuditQRListener.class.getSimpleName();

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        AuditQRRsp resp = AuditQRRsp.decodeJson(AuditQRRsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().RetCode, IDVerifySdk.LOCAL_SUCCESS)) {
                IDVerifySdkMain.getInstance().onAuditResult(json, resp);
            } else {
                IDVerifySdkMain.getInstance().onResultBack(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, json);
            }
        } else {
            IDVerifySdkMain.getInstance().onResultBack(IDVerifySdk.LOCAL_SYSTEM_EXCEPTION, IDVerifySdkMain.getInstance().default_error, null);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? "AuditQRListener--onError(JSONObject e)" : json.toString());
        IDVerifySdkMain.getInstance().onResultBack(IDVerifySdk.LOCAL_NETWORK_EXCEPTION, "", json);
    }
}

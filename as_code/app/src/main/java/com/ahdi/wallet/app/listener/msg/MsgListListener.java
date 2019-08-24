package com.ahdi.wallet.app.listener.msg;

import android.text.TextUtils;

import com.ahdi.wallet.app.sdk.MsgSdk;
import com.ahdi.wallet.app.main.MessageSdkMain;
import com.ahdi.wallet.app.response.MsgListRsp;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * @author admin
 */
public class MsgListListener implements HttpReqTaskListener {

    private static final String TAG = MsgListListener.class.getSimpleName();

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        MsgListRsp resp = MsgListRsp.decodeJson(MsgListRsp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().RetCode, MsgSdk.LOCAL_SUCCESS)) {
                MessageSdkMain.getInstance().onResultBack(MsgSdk.LOCAL_SUCCESS, resp.getmHeader().ErrMsg, json);
            } else {
                MessageSdkMain.getInstance().onResultBack(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, json);
            }
        } else {
            MessageSdkMain.getInstance().onResultBack(MsgSdk.LOCAL_SYSTEM_EXCEPTION, MessageSdkMain.getInstance().default_error, null);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? "MsgListListener--onError(JSONObject e)" : json.toString());
        MessageSdkMain.getInstance().onResultBack(MsgSdk.LOCAL_NETWORK_EXCEPTION, "", json);
    }
}

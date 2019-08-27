package com.ahdi.wallet.module.payment.withdraw.listener;

import android.text.TextUtils;

import com.ahdi.wallet.module.payment.withdraw.WithDrawSdk;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.wallet.module.payment.withdraw.main.WithDrawMain;
import com.ahdi.wallet.module.payment.withdraw.response.WDCreateResp;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/4/9.
 */

public class WDCreateListener implements HttpReqTaskListener {

    private static final String TAG = WDCreateListener.class.getSimpleName();

    private String sid;

    public WDCreateListener(String sid) {
        this.sid = sid;
    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        WDCreateResp resp = WDCreateResp.decodeJson(WDCreateResp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().retCode, WithDrawSdk.LOCAL_PAY_SUCCESS)) {
                WithDrawMain.getInstance().pay(resp.getID(), resp.getPay(), sid, Constants.LOCAL_FROM_WITHDRAW);
            } else {
                WithDrawMain.getInstance().onResultBack(resp.getmHeader().retCode, resp.getmHeader().retMsg, json, "");
            }
        } else {
            WithDrawMain.getInstance().onResultBack(WithDrawSdk.LOCAL_PAY_SYSTEM_EXCEPTION, WithDrawMain.getInstance().default_error, null, "");
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.d(TAG, json == null ? "WDCreateListener--onError(JSONObject e)" : json.toString());
        String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
        WithDrawMain.getInstance().onResultBack(WithDrawSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json, "");
    }
}

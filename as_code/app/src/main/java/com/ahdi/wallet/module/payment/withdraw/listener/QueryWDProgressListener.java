package com.ahdi.wallet.module.payment.withdraw.listener;

import android.text.TextUtils;

import com.ahdi.wallet.module.payment.withdraw.WithDrawSdk;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.wallet.module.payment.withdraw.main.WithDrawMain;
import com.ahdi.wallet.module.payment.withdraw.response.WDQueryProgressResp;

import org.json.JSONObject;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/7.
 * @email zhao_zhaohe@163.com
 */

public class QueryWDProgressListener implements HttpReqTaskListener {

    private static final String TAG = QueryWDProgressListener.class.getSimpleName();

    private String ID;

    public QueryWDProgressListener(String ID) {
        this.ID = ID;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        WDQueryProgressResp resp = WDQueryProgressResp.decodeJson(WDQueryProgressResp.class, json);
        if (resp != null) {
            if (TextUtils.equals(resp.getmHeader().RetCode, WithDrawSdk.LOCAL_PAY_SUCCESS)) {
                WithDrawMain.getInstance().onResultBack(WithDrawSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().ErrMsg, json, ID);
            } else {
                WithDrawMain.getInstance().onResultBack(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, json, ID);
            }
        } else {
            WithDrawMain.getInstance().onResultBack(WithDrawSdk.LOCAL_PAY_SYSTEM_EXCEPTION, WithDrawMain.getInstance().default_error, null, ID);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.d(TAG, json == null ? "QueryWDProgressListener--onError(JSONObject e)" : json.toString());
        String errorMsg = json == null ? "" : json.optString(Constants.MSG_KEY);
        WithDrawMain.getInstance().onResultBack(WithDrawSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, json, ID);
    }

}

package com.ahdi.wallet.bca.listener;

import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

/**
 * 异步上报响应监听
 */
public class BcaAsyncNotifyListener implements HttpReqTaskListener {

    private static final String TAG = BcaAsyncNotifyListener.class.getSimpleName();

    public BcaAsyncNotifyListener() {
    }

    @Override
    public void onPostExecute(JSONObject json) {
        LogUtil.d(TAG,"--BCA异步上报通知结果接口---"+json.toString());
    }

    @Override
    public void onError(JSONObject json) {
    }
}

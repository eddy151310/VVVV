package com.ahdi.wallet.bca.response;

import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

/**
 * @author admin
 */

public class BcaOrderRsp extends Response {

    private static final String TAG = BcaOrderRsp.class.getSimpleName();

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null){
            return;
        }
        LogUtil.e(TAG, TAG + json.toString());
    }
}

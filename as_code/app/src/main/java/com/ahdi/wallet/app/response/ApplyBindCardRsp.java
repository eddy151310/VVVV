package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.PayBindSchema;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

/**
 * Date: 2017/10/14 下午3:16
 * Author: kay lau
 * Description:
 */
public class ApplyBindCardRsp extends Response {

    private static final String TAG = ApplyBindCardRsp.class.getSimpleName();

    private PayBindSchema payBCBindSchema;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        LogUtil.e(TAG, json.toString());

        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        payBCBindSchema = ABSIO.decodeSchema(PayBindSchema.class, body.optJSONObject("BindBC"));
    }

    public PayBindSchema getPayBCBindSchema() {
        return payBCBindSchema;
    }
}

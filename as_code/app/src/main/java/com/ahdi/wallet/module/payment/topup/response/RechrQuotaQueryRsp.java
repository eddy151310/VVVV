package com.ahdi.wallet.module.payment.topup.response;

import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.app.schemas.PayBindSchema;
import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * Date: 2017/10/14 下午3:16
 * Author: kay lau
 * Description:
 */
public class RechrQuotaQueryRsp extends Response {

    private static final String TAG = RechrQuotaQueryRsp.class.getSimpleName();

    private PayBindSchema payBCBindSchema;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        LogUtil.e(TAG, json.toString());

        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        payBCBindSchema = ABSIO.decodeSchema(PayBindSchema.class, body.optJSONObject("BindBC"));
    }

    public PayBindSchema getPayBCBindSchema() {
        return payBCBindSchema;
    }
}

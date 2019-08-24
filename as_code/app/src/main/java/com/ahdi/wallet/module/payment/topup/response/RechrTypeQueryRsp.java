package com.ahdi.wallet.module.payment.topup.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.module.payment.topup.schemas.RechrTypeGroupSchema;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

/**
 * Date: 2017/10/14 下午3:16
 * Author: kay lau
 * Description:
 */
public class RechrTypeQueryRsp extends Response {

    private static final String TAG = RechrTypeQueryRsp.class.getSimpleName();

    private RechrTypeGroupSchema[] rechrTypeGroupSchemas;

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
        rechrTypeGroupSchemas = ABSIO.decodeSchemaArray(RechrTypeGroupSchema.class, "RechrTypeGroups", body);
    }

    public RechrTypeGroupSchema[] getRechrTypeGroupSchemas() {
        return rechrTypeGroupSchemas;
    }

}

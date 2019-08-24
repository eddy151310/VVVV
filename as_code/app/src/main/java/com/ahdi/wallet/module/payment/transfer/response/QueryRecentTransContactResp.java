package com.ahdi.wallet.module.payment.transfer.response;

import com.ahdi.wallet.module.payment.transfer.schemas.TAContactSchema;
import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/7.
 * @email zhao_zhaohe@163.com
 * <p>
 * 2.2.1.4	查询最近转账联系人
 */
public class QueryRecentTransContactResp extends Response {

    private TAContactSchema[] taContactSchema;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        taContactSchema = ABSIO.decodeSchemaArray(TAContactSchema.class, "TC", body);

    }

    public TAContactSchema[] getTaContactSchema() {
        return taContactSchema;
    }
}

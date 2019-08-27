package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.VoucherSchema;

import org.json.JSONObject;

/**
 * 优惠券列表响应
 * @author zhaohe
 */
public class VoucherListRsp extends Response {

    private VoucherSchema[] voucherSchemas;
    private int more;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null){
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        voucherSchemas = ABSIO.decodeSchemaArray(VoucherSchema.class, "Coupons",body);
        more = body.optInt("More");
    }

    public VoucherSchema[] getVoucherSchemas() {
        return voucherSchemas;
    }

    public int getMore() {
        return more;
    }


}

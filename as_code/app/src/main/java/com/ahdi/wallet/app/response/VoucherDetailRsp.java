package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.VoucherDetailSchema;

import org.json.JSONObject;

/**
 * 优惠券详情响应
 * @author zhaohe
 */
public class VoucherDetailRsp extends Response {

    private VoucherDetailSchema voucherDetailSchema;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null){
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        voucherDetailSchema = ABSIO.decodeSchema(VoucherDetailSchema.class, body.optJSONObject("CouponDetail"));
    }

    public VoucherDetailSchema getVoucherDetailSchema() {
        return voucherDetailSchema;
    }
}

package com.ahdi.wallet.bca.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.VerifyCodeSchema;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

/**
 * @author admin
 */

public class BcaOTPRsp extends Response {

    private static final String TAG = BcaOTPRsp.class.getSimpleName();

//    字段名	重要性	        类型	描述
//    Vcode	    必须	vcode_schema    发送短信验证码的结果

    public VerifyCodeSchema vCodeSchema;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        LogUtil.e(TAG, TAG + json.toString());

        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        vCodeSchema = ABSIO.decodeSchema(VerifyCodeSchema.class, body.optJSONObject("Vcode"));
    }
}

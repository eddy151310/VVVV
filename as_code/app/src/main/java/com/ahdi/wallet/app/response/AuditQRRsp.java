package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.PaySdkTipsSchema;
import com.ahdi.wallet.app.schemas.RnaInfoSchema;
import com.ahdi.wallet.app.schemas.RnaTypeSchema;

import org.json.JSONObject;

/**
 * 实名认证审核结果响应
 *
 * @author zhaohe
 */
public class AuditQRRsp extends Response {

//    字段名	重要性	类型	            描述
//    Audit	    必选	int	                0: 未提交1：审核通过2：审核未通过3：审核中
//    Prompt	条件	Map<String, String> 审核中、审核未通过时的提示信息  T：提示titleB：提示内容
//    RNAInfo	条件	RNAInfo_Schema       审核通过时的实名认证信息
//    Type	    条件	RNATypeSchema       审核通过时的实名认证类型


    private RnaInfoSchema rnaInfoSchema;
    private PaySdkTipsSchema tipsSchemas;
    private int audit = 0;
    private RnaTypeSchema rnaTypeSchema;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        audit = body.optInt("Audit");
        tipsSchemas = ABSIO.decodeSchema(PaySdkTipsSchema.class, body.optJSONObject("Prompt"));
        rnaInfoSchema = ABSIO.decodeSchema(RnaInfoSchema.class, body.optJSONObject("RNAInfo"));
        rnaTypeSchema = ABSIO.decodeSchema(RnaTypeSchema.class, body.optJSONObject("Type"));
    }

    public RnaInfoSchema getRnaInfoSchema() {
        return rnaInfoSchema;
    }

    public int getAudit() {
        return audit;
    }

    public PaySdkTipsSchema getTipsSchemas() {
        return tipsSchemas;
    }

    public RnaTypeSchema getRnaTypeSchema() {
        return rnaTypeSchema;
    }
}

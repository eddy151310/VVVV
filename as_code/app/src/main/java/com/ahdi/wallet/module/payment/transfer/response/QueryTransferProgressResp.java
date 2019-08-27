package com.ahdi.wallet.module.payment.transfer.response;

import com.ahdi.wallet.module.payment.transfer.schemas.TANodeSchema;
import com.ahdi.wallet.module.payment.transfer.schemas.TipsSchema;
import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/7.
 * @email zhao_zhaohe@163.com
 * <p>
 * 2.2.1.3	查询转账进度
 */
public class QueryTransferProgressResp extends Response {

    //    字段名	    重要性     	类型	    描述
    //    Nodes	        必须	TA_NODE[]	    转账处理时间节点
    //    Tips	        必须	Map<String,String>	T: 转账进度提示文案title  B：转账进度提示文案body
    //    Result	    必须	Int	            转账结果（0：待转账10：处理中20：失败30：成功）
    //    PayType	    可选	String	        支付方式
    //    Amount	    可选	String	        转账到账金额
    //    PayeeAccount	可选	String	        收款人账号；格式：(nickname+loginname）昵称（隐藏手机号）
    //    ServiceFee	可选	String	        平台手续费（基本单位的原始金额）
    //    MktAmt	    可选	String	        营销抵扣金额（基本单位的原始金额）
    //    PayAmt	    可选	String	        实际付款金额（基本单位的原始金额）

    public TANodeSchema[] taNodeSchemas;
    public TipsSchema tipsSchemas;
    public String result;
    public String payType;
    public String amount;
    public String payeeAccount;
    public String serviceFee;
    public String mktAmt;
    public String payAmt;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        taNodeSchemas = ABSIO.decodeSchemaArray(TANodeSchema.class, "Nodes", body);
        tipsSchemas = ABSIO.decodeSchema(TipsSchema.class, body.optJSONObject("Tips"));

        int optInt = body.optInt("Result");
        result = String.valueOf(optInt);

        payType = body.optString("PayType");
        amount = body.optString("Amount");
        payeeAccount = body.optString("PayeeAccount");
        serviceFee = body.optString("ServiceFee");
        mktAmt = body.optString("MktAmt");
        payAmt = body.optString("PayAmt");

    }

    public TipsSchema getTipsSchemas() {
        return tipsSchemas;
    }

}

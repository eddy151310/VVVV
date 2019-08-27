package com.ahdi.wallet.module.payment.withdraw.response;

import com.ahdi.wallet.module.payment.transfer.schemas.TipsSchema;
import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/4/9.
 */

public class WDQueryProgressResp extends Response {

    //    字段名	重要性	类型	描述
    //    Tips	    必须	Map<String,String>	T: 提现进度提示文案title  B：提现进度提示文案body
    //    Result	必须	Int	提现结果（10:处理中20:失败30:成功）
    //    BAccount	必须	String	银行账户
    //    PayAmount	必须	String	支付金额
    //    Amount	必须	String	到账金额
    //    ChargeFee	必须	String	手续费
    //    WdName	可选	String	提现人名称

    public TipsSchema tipsSchemas;
    public String result;
    public String bankAccount;
    public String payAmount;
    public String amount;
    public String chargeFee;
    public String wdName;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        tipsSchemas = ABSIO.decodeSchema(TipsSchema.class, body.optJSONObject("Tips"));
        int optInt = body.optInt("Result");
        result = String.valueOf(optInt);
        bankAccount = body.optString("BAccount");
        payAmount = body.optString("PayAmount");
        amount = body.optString("Amount");
        chargeFee = body.optString("ChargeFee");
        wdName = body.optString("WdName");
    }

    public TipsSchema getTipsSchemas() {
        return tipsSchemas;
    }

    public String getResult() {
        return result;
    }

}

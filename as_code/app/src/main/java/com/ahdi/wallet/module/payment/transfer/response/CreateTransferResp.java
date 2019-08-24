package com.ahdi.wallet.module.payment.transfer.response;

import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/7.
 * @email zhao_zhaohe@163.com
 * <p>
 * 创建转账 响应
 */
public class CreateTransferResp extends Response {

    //    字段名	    重要性	类型  	描述
    //    ID	    必须	    String  转账（Token用户结果查询）
    //    Pay	    必须  	String  支付参数


    /**
     * 转账（Token用户结果查询）
     */
    private String id;
    /**
     * 支付参数
     */
    private String pay;


    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        id = body.optString("ID");
        pay = body.optString("Pay");
    }

    public String getId() {
        return id;
    }

    public String getPay() {
        return pay;
    }
}

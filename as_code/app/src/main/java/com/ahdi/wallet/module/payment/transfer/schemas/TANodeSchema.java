package com.ahdi.wallet.module.payment.transfer.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author admin
 * @data Created by xiaoniu on 2018/1/5.
 * @email zhao_zhaohe@163.com
 * <p>
 * 转账节点 TA_NODE
 */
public class TANodeSchema extends ABSIO {

    //    字段名	重要性	类型	描述
    //    Node	    必须	String	节点名称：支付完成   对方到账
    //    Time	    必须	long	节点时间

    private String node;//节点名称：支付完成   对方到账
    private long time;//节点时间

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        node = json.optString("Node");
        time = json.optLong("Time");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

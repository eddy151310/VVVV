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
public class TipsSchema extends ABSIO {

    public String title;
    public String body;

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        title = json.optString("T");
        body = json.optString("B");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }
}

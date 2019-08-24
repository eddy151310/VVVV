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
public class TAContactSchema extends ABSIO {

    public String uid;
    public String lName;
    public String sName;  // 用户登录名（含隐藏码）,用于展示
    public String nName;
    public String avatar; // 用户头像
    public long time;     // 节点时间

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        uid = json.optString("UID");
        lName = json.optString("LName");
        sName = json.optString("SName");
        nName = json.optString("NName");
        avatar = json.optString("Avatar");
        time = json.optLong("Time");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }
}

package com.ahdi.wallet.module.payment.transfer.requset;

import android.text.TextUtils;

import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.network.framwork.Request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/7.
 * @email zhao_zhaohe@163.com
 *
 * 查询转账进度
 */

public class QueryTransferProgressRequest extends Request {

    private static final String TAG = QueryTransferProgressRequest.class.getSimpleName();

    //    字段名	    重要性	类型	    描述
    //    ID	    必须	    String  转账Token


    /**
     * 转账Token
     */
    private String id;

    public QueryTransferProgressRequest(String id,String sid) {
        super(sid);
        this.id = id;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            if (!TextUtils.isEmpty(id)){
                body.put("ID",id);
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }
}

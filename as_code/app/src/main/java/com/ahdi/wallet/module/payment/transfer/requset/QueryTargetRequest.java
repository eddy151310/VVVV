package com.ahdi.wallet.module.payment.transfer.requset;

import android.text.TextUtils;

import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.network.framwork.Request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/5.
 * @email zhao_zhaohe@163.com
 * <p>
 * 查询转账目标
 */

public class QueryTargetRequest extends Request {

    private static final String TAG = QueryTargetRequest.class.getSimpleName();

//    字段名	重要性	类型	描述
//    Q	        必须	String	转账目标账户登录名(邮箱或手机号)

    /**
     * 转账目标账户登录名(邮箱或手机号)
     */
    private String q;

    public QueryTargetRequest(String lName, String sid) {
        super(sid);
        this.q = lName;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            if (!TextUtils.isEmpty(q)) {
                body.put("Q", q);
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, "查询转账目标请求:" + json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }
}

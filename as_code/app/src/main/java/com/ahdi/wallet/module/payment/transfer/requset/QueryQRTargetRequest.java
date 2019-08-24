package com.ahdi.wallet.module.payment.transfer.requset;

import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.network.framwork.Request;

import org.json.JSONObject;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/5.
 * @email zhao_zhaohe@163.com
 *
 * 查询二维码扫描出来的转账目标
 */

public class QueryQRTargetRequest extends Request {

    private static final String TAG = QueryQRTargetRequest.class.getSimpleName();

    private String requestParam;

    public QueryQRTargetRequest(String requestParam) {
        this.requestParam = requestParam;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }
        return json;
    }

    @Override
    public String execute() {
        LogUtil.e(TAG, "requestParam = " + requestParam);
        return requestParam;
    }
}

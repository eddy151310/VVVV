package com.ahdi.wallet.bca.requset;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 作者：lixue on 2019/1/14 18:27
 * <p>
 * <p>
 * 绑卡结果通知
 */

public class BcaResultNotifyReq extends Request {

    private static final String TAG = BcaResultNotifyReq.class.getSimpleName();

    private String init;        // BC/UPDATE
    private String method;      // Bca sdk的回调函数名称
    private String data;        // BCA回调数据。 Json map string格式，key为参数名，value为参数值。
    private String bid;

    public BcaResultNotifyReq(String sid, String init, String method, String data, String bid) {
        super(sid);
        this.init = init;
        this.method = method;
        this.data = data;
        this.bid = bid;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }
        JSONObject body = new JSONObject();
        try {
            body.put("Init", init);
            body.put("Method", method);
            body.put("Data", data);
            body.put("UserAgent", AppGlobalUtil.getInstance().getBCAUserAgent());
            body.put("DeviceId", AppGlobalUtil.getInstance().getBCADeviceID());
            if (TextUtils.equals("UPDATE", init) && !TextUtils.isEmpty(bid)) {
                body.put("BID", bid);
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

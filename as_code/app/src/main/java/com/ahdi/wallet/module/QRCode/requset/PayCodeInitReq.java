package com.ahdi.wallet.module.QRCode.requset;

import com.ahdi.wallet.module.QRCode.bean.PayCodeInitBean;
import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2018/1/4 下午5:56
 * Author: kay lau
 * Description:
 */
public class PayCodeInitReq extends Request {

    private PayCodeInitBean bean;

    public PayCodeInitReq(PayCodeInitBean bean, String sid) {
        super(sid);
        this.bean = bean;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {

        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            if (bean != null) {
                body.put("PubKey", bean.getPublicKey());
                body.put("ClientKey", bean.getClientKey());
                body.put("UserAgent", AppGlobalUtil.getInstance().getBCAUserAgent());
                body.put("DeviceId", AppGlobalUtil.getInstance().getBCADeviceID());
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LogUtil.e("PayCodeInitReq", json.toString());
        return json;
    }
}

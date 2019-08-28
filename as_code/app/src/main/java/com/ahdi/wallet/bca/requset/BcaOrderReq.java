package com.ahdi.wallet.bca.requset;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.wallet.app.schemas.TerminalInfoSchema;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author admin
 * @date 2019/01/15
 */

public class BcaOrderReq extends Request {

    private static final String TAG = BcaOrderReq.class.getSimpleName();

    private String payEx;
    private String payInfo;
    private int channelID;
    private TerminalInfoSchema terminalInfoSchema;

    public BcaOrderReq(String sid, String payEx, String payInfo, int channelID) {
        super(sid);
        this.payEx = payEx;
        this.payInfo = payInfo;
        this.channelID = channelID;
        terminalInfoSchema = new TerminalInfoSchema();
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }
        JSONObject body = new JSONObject();
        try {
            body.put("PayEx", payEx);

            if (!TextUtils.isEmpty(payInfo)) {
                body.put("PayInfo", payInfo);
            }

            body.put("ChannelID", channelID);

            if (terminalInfoSchema != null) {
                terminalInfoSchema.writeTo(body);
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, "BcaOrderReq: " + json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }
}

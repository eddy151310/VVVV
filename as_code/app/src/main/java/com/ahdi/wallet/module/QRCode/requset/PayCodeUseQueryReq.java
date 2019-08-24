package com.ahdi.wallet.module.QRCode.requset;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Date: 2018/1/4 下午4:46
 * Author: kay lau
 * Description:
 */
public class PayCodeUseQueryReq extends Request {

    private long clientId;
    private ArrayList<String> timesLot;
    private String touchVer;

    public PayCodeUseQueryReq(long clientId, ArrayList<String> timesLot, String touchVer, String sid) {
        super(sid);
        this.clientId = clientId;
        this.timesLot = timesLot;
        this.touchVer = touchVer;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("clientid", clientId);
            if (timesLot != null) {
                JSONArray timeArray = new JSONArray();
                for (int i = 0; i < timesLot.size(); i++) {
                    timeArray.put(timesLot.get(i));
                }
                body.put("timeslot", timeArray);
            }
            if (!TextUtils.isEmpty(touchVer)) {
                body.put("TouchVer", touchVer);
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("PayAuthCodeUseQuery", json.toString());
        return json;
    }
}

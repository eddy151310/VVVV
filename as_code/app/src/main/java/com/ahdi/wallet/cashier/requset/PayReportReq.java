package com.ahdi.wallet.cashier.requset;

import android.text.TextUtils;

import com.ahdi.wallet.cashier.bean.ReportDataBean;
import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2019/4/23 下午2:11
 * Author: kay lau
 * Description:
 */
public class PayReportReq extends Request {

    private static final String TAG = PayReportReq.class.getSimpleName();

    private String type;
    private ReportDataBean reportData;

    public PayReportReq(String type, ReportDataBean reportData, String sid) {
        super(sid);
        this.type = type;
        this.reportData = reportData;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }
        try {
            JSONObject body = new JSONObject();

            JSONObject reportJson = buildReportData();

            body.put("Type", type);
            if (reportJson != null) {
                body.put("Report", reportJson.toString());
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, "支付上报: " + json.toString());
        return json;
    }

    private JSONObject buildReportData() throws JSONException {

        if (reportData == null) {
            return null;
        }

        JSONObject reportJson = new JSONObject();

        if (!TextUtils.isEmpty(reportData.getUID())) {
            reportJson.put("UID", reportData.getUID());
        }
        if (!TextUtils.isEmpty(reportData.getTID())) {
            reportJson.put("TID", reportData.getTID());
        }
        if (!TextUtils.isEmpty(reportData.getTransID())) {
            reportJson.put("TransID", reportData.getTransID());
        }
        if (!TextUtils.isEmpty(reportData.getPayResult())) {
            reportJson.put("PayResult", reportData.getPayResult());
        }
        if (!TextUtils.isEmpty(reportData.getOrderID())) {
            reportJson.put("OrderID", reportData.getOrderID());
        }
        if (!TextUtils.isEmpty(reportData.getChannel())) {
            reportJson.put("Channel", reportData.getChannel());
        }
        if (!TextUtils.isEmpty(reportData.getPayType())) {
            reportJson.put("PayType", reportData.getPayType());
        }
        if (!TextUtils.isEmpty(reportData.getReportData())) {
            reportJson.put("Data", reportData.getReportData());
        }
        return reportJson;
    }
}

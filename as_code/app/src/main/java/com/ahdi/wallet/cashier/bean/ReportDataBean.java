package com.ahdi.wallet.cashier.bean;

/**
 * Date: 2018/1/5 下午3:49
 * Author: kay lau
 * Description:
 */
public class ReportDataBean {

    private String UID;
    private String TID;
    private String TransID;
    private String OrderID;
    private String Channel;
    private String PayType;
    private String reportData;
    private String PayResult;

    public String getPayResult() {
        return PayResult;
    }

    public void setPayResult(String payResult) {
        PayResult = payResult;
    }

    public ReportDataBean() {
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getTID() {
        return TID;
    }

    public void setTID(String TID) {
        this.TID = TID;
    }

    public String getTransID() {
        return TransID;
    }

    public void setTransID(String transID) {
        TransID = transID;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getChannel() {
        return Channel;
    }

    public void setChannel(String channel) {
        Channel = channel;
    }

    public String getPayType() {
        return PayType;
    }

    public void setPayType(String payType) {
        PayType = payType;
    }

    public String getReportData() {
        return reportData;
    }

    public void setReportData(String reportData) {
        this.reportData = reportData;
    }
}

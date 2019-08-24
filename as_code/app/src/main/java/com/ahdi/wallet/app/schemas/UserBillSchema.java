package com.ahdi.wallet.app.schemas;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.lib.utils.utils.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimeZone;

public class UserBillSchema extends ABSIO {

    private String id;          // 交易流水id
    private long time;          // 交易时间
    private String status;      // 交易状态
    private String icon;        // 交易类别icon图url
    private String title;       // 交易类别及名称
    private String money;       // 交易金额
    private String inOut;       // 金额符号：+收入；-支出；null不用显示符号(列表详情输出)
    private String body;        // 带HTML格式的交易流水详情信息，具体详情展示内容，根据交易类别制定不同的详情展示页（详情时输出）
    private String dTime;       // "年-月"
    private boolean showDtime;  // 是否展示dTime
    private int bizType;        // 账单类型，对应账单列表查询条件的type值
    private String url;         // 账单详情H5页面url请求地址

    @Override
    public void readFrom(JSONObject json) throws JSONException {

        if (json == null) {
            return;
        }
        id = json.optString("ID");
        time = json.optLong("Time");
        status = json.optString("Status");
        icon = json.optString("Icon");
        title = json.optString("Title");
        bizType = json.optInt("BizType");
        money = json.optString("Money");
        inOut = json.optString("InOut");
        body = json.optString("Body");
        url = json.optString("Url");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

    public String getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getMoney() {
        return money;
    }

    public String getInOut() {
        return inOut;
    }

    public String getBody() {
        return body;
    }

    public String getdTime(TimeZone timeZone) {
        if (!TextUtils.isEmpty(dTime)){
            return dTime;
        }
        dTime = DateUtil.formatTimeNoDHMS(time, timeZone);
        return dTime;
    }

    public int getBizType() {
        return bizType;
    }

    public boolean isShowDtime() {
        return showDtime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public void setInOut(String inOut) {
        this.inOut = inOut;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setdTime(String dTime) {
        this.dTime = dTime;
    }

    public void setShowDtime(boolean showDtime) {
        this.showDtime = showDtime;
    }

    public void setBizType(int bizType) {
        this.bizType = bizType;
    }

    public String getUrl() {
        return url;
    }
}

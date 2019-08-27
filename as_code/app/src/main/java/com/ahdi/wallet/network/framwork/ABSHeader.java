package com.ahdi.wallet.network.framwork;

import com.ahdi.lib.utils.config.ConfigHelper;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.DateUtil;
import com.ahdi.lib.utils.utils.DeviceUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class ABSHeader extends ABSIO {

    public String retCode = Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION;
    public String retMsg = "";

    private String sid;
    private int tzOffset = DateUtil.getTZOffset(null);

    public ABSHeader() {
    }

    public ABSHeader(String sid) {
        this.sid = sid;
    }

    public ABSHeader(String sid, int tzOffset) {
        this.sid = sid;
        if (tzOffset >= -18 && tzOffset <= 18) {
            this.tzOffset = tzOffset;
        }
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        if (json == null) {
            return null;
        }
        JSONObject headerDate = new JSONObject();

        headerDate.put("clientVersion" , ConfigHelper.APP_VERSION_NAME);
        headerDate.put("clientType" , "0");
        headerDate.put("device" , DeviceUtil.getAndroidId()); //设备型号
        headerDate.put("manufacturer" , android.os.Build.MANUFACTURER); //手机厂商
        headerDate.put("model" , DeviceUtil.getPhoneBrand()); //手机品牌
        headerDate.put("os_version" , DeviceUtil.getBuildVersion()); //系统版本号
        json.put("header", headerDate); // header

        return json;
    }

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        retCode = json.optString("retCode");
        retMsg = json.optString("retMsg");
    }
}

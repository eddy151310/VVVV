package com.ahdi.wallet.app.schemas;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.lib.utils.config.ConfigHelper;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.network.InternetUtil;
import com.ahdi.lib.utils.utils.DeviceUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2017/10/14 下午2:39
 * Author: kay lau
 * Description:
 */
public class TerminalInfoSchema extends ABSIO {

    @Override
    public void readFrom(JSONObject json) throws JSONException {
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        if (json == null) {
            return null;
        }
        JSONObject terminalJson = new JSONObject();

        if (!TextUtils.isEmpty(DeviceUtil.getIMSI())) {
            terminalJson.put("IMSI", DeviceUtil.getIMSI());
        }
        if (!TextUtils.isEmpty(DeviceUtil.getDeviceID())) {
            terminalJson.put("IMEI", DeviceUtil.getDeviceID());
        }
        if (!TextUtils.isEmpty(DeviceUtil.getMeid())) {
            terminalJson.put("MEID", DeviceUtil.getMeid());
        }
        if (!TextUtils.isEmpty(DeviceUtil.getAndroidId())) {
            terminalJson.put("AndroidID", DeviceUtil.getAndroidId());
        }
        if (!TextUtils.isEmpty(DeviceUtil.getPhoneName())) {
            terminalJson.put("DeviceName", DeviceUtil.getPhoneName());
        }
        if (!TextUtils.isEmpty(DeviceUtil.getPhoneBrand())) {
            terminalJson.put("DeviceBrand", DeviceUtil.getPhoneBrand());
        }
        if (!TextUtils.isEmpty(DeviceUtil.getMacAddress())) {
            terminalJson.put("Mac", DeviceUtil.getMacAddress());
        }
        if (!TextUtils.isEmpty(InternetUtil.getNetType())) {
            terminalJson.put("NetType", InternetUtil.getNetType());
        }
        if (!TextUtils.isEmpty(DeviceUtil.getModel())) {
            terminalJson.put("Model", DeviceUtil.getModel());
        }
        if (!TextUtils.isEmpty(DeviceUtil.getBuildVersion())) {
            terminalJson.put("OSVer", DeviceUtil.getBuildVersion());
        }
        if (!TextUtils.isEmpty(Constants.LOCAL_OS_ANDROID)) {
            terminalJson.put("OS", Constants.LOCAL_OS_ANDROID);
        }
        if (!TextUtils.isEmpty(ConfigHelper.APP_VERSION_NAME)) {
            terminalJson.put("AppVer", ConfigHelper.APP_VERSION_NAME);
        }
        if (!TextUtils.isEmpty(AppGlobalUtil.getInstance().getPackageName())) {
            terminalJson.put("AppPkg", AppGlobalUtil.getInstance().getPackageName());
        }

        json.put("TInfo", terminalJson);
        return json;
    }
}

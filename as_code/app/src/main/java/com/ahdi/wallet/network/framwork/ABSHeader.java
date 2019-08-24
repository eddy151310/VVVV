package com.ahdi.wallet.network.framwork;

import android.text.TextUtils;

import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.config.ConfigHelper;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.DateUtil;
import com.ahdi.lib.utils.utils.LanguageUtil;
import com.ahdi.lib.utils.utils.TerminalIdUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ABSHeader extends ABSIO {

    /**
     * key
     */
    private static final String C_Inf = "CInf";
    private static final String Env = "Env";
    private static final String IID = "IID";
    private static final String Ver = "Ver";
    private static final String S_ID = "SID";
    private static final String T_ID = "TID";
    private static final String PKGID = "PKGID";

    private static final String TZ = "TZ";
    private static final String HEAD_KEY = "H";

    /**
     * value
     */
    private static final int DeviceType = 100;                //必须 Integer 用户终端设备类型：100-手机终端
    private static final int OsType = 1;                      //必须	Integer	终端操作系统类型：	安卓 - 1	IOS - 2

    public String RetCode = Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION;
    public String ErrMsg = "";

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
        JSONObject jsonHeader = new JSONObject();

        // Env(可选) 可不设置。APP环境参数，格式[language, Country] 切换语言时语言编码必须传入
        JSONArray Env_json = new JSONArray();
        String language = LanguageUtil.getLanguage(AppGlobalUtil.getInstance().getContext());
        if (TextUtils.isEmpty(language)) {
            Env_json.put(Constants.LOCAL_LAN_DEFAULT);
        } else {
            if (TextUtils.equals(language, Constants.LOCAL_LAN_ID)) {
                //如果是印尼语, 此处传入"id
                Env_json.put("id");
            } else {
                Env_json.put(language);
            }
        }
        jsonHeader.put(Env, Env_json);

        // CInf(必须) 客户端信息，格式:[DeviceType, OSType]。
        JSONArray cinf_json = new JSONArray();
        cinf_json.put(DeviceType);
        cinf_json.put(OsType);
        jsonHeader.put(C_Inf, cinf_json);

        // TZ(可选) 客户端时区，如+07为时区偏移小时数；取值范围-18 to +18
        jsonHeader.put(TZ, tzOffset);

        // Ver(必须) APP 客户端版本号
        jsonHeader.put(Ver, ConfigHelper.APP_VERSION_NAME);

        // TID(必须) 终端标识 请求消息需要传入该字段
        jsonHeader.put(T_ID, TerminalIdUtil.getTerminalID());

        // PKGID(必须) 应用包名id
        jsonHeader.put(PKGID, ConfigCountry.PKGID);

        // SID(可选)
        if (!TextUtils.isEmpty(sid)) {
            jsonHeader.put(S_ID, sid);
        }

        json.put(HEAD_KEY, jsonHeader); // head
        return json;
    }

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        RetCode = json.optString(Constants.RET_CODE_KEY);
        ErrMsg = json.optString(Constants.MSG_KEY);
    }
}

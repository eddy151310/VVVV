package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhaohe
 * 配置信息  协议和帮助中心等
 */
public class ConfigSchema extends ABSIO {

    /**
     * 版本号
     */
    public String cfgversion = "";
    /**
     * SPG充值提示页面
     */
    public String topUpSPGUrl = "";
    /**
     * 帮助页面
     */
    public String helpUrl = "";
    /**
     * 找回密码客服页面
     */
    public String rPwdServiceUrl = "";
    /**
     * 用户协议
     */
    public String userAgreementUrl = "";
    /**
     * 指纹协议
     */
    public String touchAgreementUrl = "";

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        cfgversion = json.optString("cfgversion");
        topUpSPGUrl = json.optString("BalanceTopUpSPGUrl");
        helpUrl = json.optString("HelpUrl");
        rPwdServiceUrl = json.optString("RPwdServiceUrl");
        userAgreementUrl = json.optString("UserAgreementUrl");
        touchAgreementUrl = json.optString("TouchAgreementUrl");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

}

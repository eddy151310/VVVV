package com.ahdi.wallet.util;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.wallet.app.schemas.ConfigSchema;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.config.Constants;

/**
 * app的配置信息
 * 帮助，协议等H5地址
 */
public class AppConfigHelper {

    private static AppConfigHelper instance = null;

    /**版本号*/
    public String cfgversion = "";
    /**SPG充值提示页面*/
    public String topUpSPGUrl = "";
    /**帮助页面*/
    public String helpUrl = "";
    /**找回密码客服页面*/
    public String rPwdServiceUrl = "";
    /**用户协议*/
    public String userAgreementUrl = "";
    /**指纹协议*/
    public String touchAgreementUrl = "";

    private AppConfigHelper() {
    }

    public synchronized static AppConfigHelper getInstance() {
        if (instance == null) {
            instance = new AppConfigHelper();
        }
        return instance;
    }

    public void updateClientCfg(ConfigSchema clientCfg) {
        if (clientCfg == null){
            return;
        }
        this.cfgversion = clientCfg.cfgversion;
        this.topUpSPGUrl = clientCfg.topUpSPGUrl;
        this.helpUrl = clientCfg.helpUrl;
        this.rPwdServiceUrl = clientCfg.rPwdServiceUrl;
        this.userAgreementUrl = clientCfg.userAgreementUrl;
        this.touchAgreementUrl = clientCfg.touchAgreementUrl;

        Context context = AppGlobalUtil.getInstance().getContext();
        AppGlobalUtil.getInstance().putString(context, Constants.LOCAL_APP_CLIENT_VERSION_KEY, cfgversion);
        AppGlobalUtil.getInstance().putString(context, Constants.LOCAL_APP_TOP_UP_SPG_URL, topUpSPGUrl);
        AppGlobalUtil.getInstance().putString(context, Constants.LOCAL_APP_CLIENT_HELP_URL, helpUrl);
        AppGlobalUtil.getInstance().putString(context, Constants.LOCAL_APP_RPWD_SERVICE_URL, rPwdServiceUrl);
        AppGlobalUtil.getInstance().putString(context, Constants.LOCAL_APP_USER_AGREEMENT_KEY, userAgreementUrl);
        AppGlobalUtil.getInstance().putString(context, Constants.LOCAL_APP_TOUCH_AGREEMENT_URL, touchAgreementUrl);
    }

    /**
     * 配置版本
     * @return
     */
    public String getCfgVersion() {
        if (!TextUtils.isEmpty(cfgversion)){
            return cfgversion;
        }
        return  AppGlobalUtil.getInstance().getString(AppGlobalUtil.getInstance().getContext(), Constants.LOCAL_APP_CLIENT_VERSION_KEY);
    }
    /**
     * spg地址
     * @return
     */
    public String getTopUpSPGUrl() {
        if (!TextUtils.isEmpty(topUpSPGUrl)){
            return topUpSPGUrl;
        }
        return  AppGlobalUtil.getInstance().getString(AppGlobalUtil.getInstance().getContext(), Constants.LOCAL_APP_TOP_UP_SPG_URL);
    }

    /**
     * Setting 页面的help
     * @return
     */
    public String getHelpUrl() {
        if (!TextUtils.isEmpty(helpUrl)){
            return helpUrl;
        }
        return  AppGlobalUtil.getInstance().getString(AppGlobalUtil.getInstance().getContext(), Constants.LOCAL_APP_CLIENT_HELP_URL);
    }

    /**
     * 找回密码手机号收不到短信验证码
     * @return
     */
    public String getrPwdServiceUrl() {
        if (!TextUtils.isEmpty(rPwdServiceUrl)){
            return rPwdServiceUrl;
        }
        return  AppGlobalUtil.getInstance().getString(AppGlobalUtil.getInstance().getContext(), Constants.LOCAL_APP_RPWD_SERVICE_URL);
    }

    /**
     * 协议
     * @return
     */
    public String getUserAgreeMentUrl() {
        if (!TextUtils.isEmpty(userAgreementUrl)){
            return userAgreementUrl;
        }
        return  AppGlobalUtil.getInstance().getString(AppGlobalUtil.getInstance().getContext(), Constants.LOCAL_APP_USER_AGREEMENT_KEY);
    }

    /**
     * 指纹协议
     * @return
     */
    public String getTouchAgreementUrl() {
        if (!TextUtils.isEmpty(touchAgreementUrl)){
            return touchAgreementUrl;
        }
        return  AppGlobalUtil.getInstance().getString(AppGlobalUtil.getInstance().getContext(), Constants.LOCAL_APP_TOUCH_AGREEMENT_URL);
    }

    public void destroy() {
        instance = null;
    }
}

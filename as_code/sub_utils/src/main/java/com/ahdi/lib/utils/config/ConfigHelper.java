package com.ahdi.lib.utils.config;

import com.ahdi.lib.utils.BuildConfig;

public interface ConfigHelper {

    boolean DEBUG = BuildConfig.debug_log;
    boolean CRASH_LOG = BuildConfig.crash_log;

    //------------------------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------------------------

    String URL = BuildConfig.url;
    String BCA_RSA_PUBLIC_KEY = BuildConfig.bca_rsa_publicKey;
    int BCA_RSA_PUBLIC_KEY_ID = BuildConfig.bca_rsa_publicKey_id;
    String APP_VERSION_NAME = BuildConfig.VERSION_NAME;

    // RSA 公钥 登录密码 支付密码相关配置
    String PAY_PASSWORD_RSA = BuildConfig.pay_password_rsa;
    String PAY_PASSWORD_RSA_ID_PREFIX = BuildConfig.pay_password_rsa_id_prefix;
    String LOGIN_PASSWORD_RSA = BuildConfig.login_password_rsa;
    String LOGIN_PASSWORD_RSA_ID_PREFIX = BuildConfig.login_password_rsa_id_prefix;

    //------------------------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------------------------

    // 友盟配置
    String UMENG_APPKEY = "5c0f2d1fb465f592110000cc";
    String UMENG_CHANNEL = "GooglePlay";

}
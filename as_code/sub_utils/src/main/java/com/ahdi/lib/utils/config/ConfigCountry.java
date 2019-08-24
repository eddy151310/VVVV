package com.ahdi.lib.utils.config;

/**
 * Date: 2019/5/17 下午4:48
 * Author: kay lau
 * Description:
 */
public interface ConfigCountry {


    // 日志写入sd卡的文件名称   规范如下：
    // .a_国家缩写_业务（项目）_分支版本   ( m  =  merchant  、 w = wallet )
    // 印尼微信商户端：  	.a_in_mbmpay_m
    // 印尼马拉松钱包：  	.a_in_mbmpay_w
    String PLATFORMID_FOLDER = ".a_in_qrindo_w";

    /**
     * 包名对应的id，方便服务端识别是哪个app
     */
    int PKGID = 6210;

    String SCHEMA_APP = "qrindo"; //会影响 扫码转账、扫商户等 请查看 SchemaUtil 类

    String KEY_ADD = "+";

    String KEY_AREA_CODE = "62";                        // 国家区号

    String KEY_ADD_AREA_CODE = KEY_ADD + KEY_AREA_CODE; // 带加号的 国家区号

    String KEY_CURRENCY_SYMBOL = "Rp. ";                // 国家货币符号

    String KEY_CURRENCY = "IDR";                        // 国家货币币种

    String KEY_COUNTRY = "Indonesia";                   // 国家名称

    int PHONE_LIMIT_MIN_LENGTH_7 = 7;                   // 限制手机号最小长度 非印尼手机号

    int PHONE_LIMIT_MIN_LENGTH_8 = 8;                   // 限制手机号最小长度 印尼手机号

    int PHONE_LIMIT_MAX_LENGTH_13 = 13;                 // 限制登录账号最大长度13

    int PHONE_LIMIT_MAX_LENGTH_14 = 14;                 // 限制登录账号最大长度14 印尼手机号

    int PHONE_LIMIT_MAX_LENGTH_15 = 15;                 // 限制登录账号最大长度15 非印尼手机号

    int LIMIT_AMOUNT_LENGTH = 8;                        // 金额限制长度

    String AMOUNT_FORMAT_SYMBOL = ".";                  // 金额格式化的分割符号

    int AMOUNT_FORMAT_LENGTH = 3;                       // 金额格式化的位数（几位一格式）

    String PHONE_PREFIX_8 = "8";                        // 手机号以8开头

    String PHONE_PREFIX_08 = "08";                      // 手机号以08开头


}

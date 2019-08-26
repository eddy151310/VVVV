package com.ahdi.wallet.app.utils;

import com.ahdi.lib.utils.config.Constants;

/**
 * Date: 2018/5/14 下午1:57
 * Author: kay lau
 * Description:
 */
public interface ConstantsPayment {

    String MODULE_SMS_CODE = "user/getSmsCode";
    String MODULE_LOGIN_SMS = "user/login";


    String MODULE_QUERY_REGISTER = "u/check";
    String MODULE_REGISTER = "u/reg";
    String MODULE_LOGIN = "u/login";
    String MODULE_IID_BIND = "u/client_bind";
    String MODULE_LOGOUT = "u/logout";
    String MODULE_PWD_CHECK = "u/pwd/check";
    String MODULE_PWD_MODIFY = "u/pwd/modify";
    String MODULE_PWD_SET = "u/pwd/set";
    String MODULE_PWD_CREATE = "u/pwd/create";
    String MODULE_QUERY_BALANCE = "a/qry";
    String MODULE_SET_PAY_PWD = "a/setpwd";
    String MODULE_RESET_PAY_PWD = "a/uppwd";
    String MODULE_PAY_PWD_CHECK = "a/ckppwd";
    String MODULE_QUERY_BIND_CARD_INFO = "a/bc/qbinds";
    String MODULE_SELECT_BIND_CARD_TYPE = "a/bc/qrtype";
    String MODULE_BIND_CARD_DETAILS = "a/bc/qr";
    String MODULE_QUERY_BIND_ACCOUNT_INFO = "a/ba/qr";
    String MODULE_BIND_CARD = "a/bc/bind";
    String MODULE_BIND_ACCOUNT = "a/ba/add";
    String MODULE_UNBIND_CARD = "a/bc/unbind";
    String MODULE_SETLIMIT_CARD = "bca/bc/editlimit";
    String MODULE_ISCANBIND_CARD = "a/bc/bind_check";
    String MODULE_UNBIND_ACCOUNT = "a/ba/del";

    String MODULE_MSG_HOME_URL = "msg/home";
    String MODULE_ACCOUNT_PROFILE = "a/v1/profile";
    String MODULE_SVC_REGISTER = "u/svc";
    String MODULE_VVC_REGISTER = "u/vvc";
    String MODULE_PWD_SVC = "u/pwd/svc";
    String MODULE_PWD_VVC = "u/pwd/vvc";

    String MODULE_PAY_PWD_SVC = "a/pwd/svc";
    String MODULE_PAY_PWD_VVC = "a/pwd/vvc";

    String MODULE_BILL_LIST = "a/bill/list";

    String MODULE_RNA_TYPES = "rna/types";
    String MODULE_RNA_SUBMIT_ALL = "rna/submitAll";
    String MODULE_AUDIT_QR = "rna/qr";

    String MODULE_UP_URL = "u/up_url";
    String MODULE_UP = "u/up";
    String MODULE_SET_ICON = "u/icon";
    String MODULE_CLIENT_CFG = "client/cfg";
    String MODULE_USER_AGREEMENT = "user/agreement";
    String MODULE_USER_INFO_GUIDE_SET = "u/set";

    //其他接口 -- 获取银行卡列表
    String MODULE_QUERY_BANK_ACCOUNT_LIST = "bank";
    //其他接口 -- 扫码检测
    String MODULE_SCAN_CHECK = "code/check";
    /**
     * 获取首页banner P是Banner位置编号，可以为多个,以”,”分割。
     */
    String MODULE_BANNER_INFO = "banner/info";
    /**
     * BANK_VA接口
     */
    String MODULE_BANK_VA = "rechr/guide";
    /**
     * 电话区号接口
     */
    String MODULE_PHONE_AREA_CODE = "country";

    /**
     * 指纹支付开启
     */
    String MODULE_TouchID_PAY_OPEN = "a/biometric/open";
    /**
     * 指纹支付关闭
     */
    String MODULE_TouchID_PAY_CLOSE = "a/biometric/close";

    String MODULE_VOUCHER_LIST = "mkt/coup/ls";
    String MODULE_VOUCHER_DETAIL = "mkt/coup/detail";

    /**
     * 支付上报
     */
    String MODULE_PAY_REPORT = "pay_report";



    /**
     * 支付结果
     * PAY_CANCEL:-----------取消支付
     * CLOSED:---------------交易关闭
     */
    //* WAIT:-----------------等待支付
    String WAIT = "WAIT";
    //* PAY_ING:--------------支付中
    String PAY_ING = "PAY_ING";
    //* PAY_OK:---------------支付成功
    String PAY_OK = "PAY_OK";
    //* PAY_FAIL:-------------支付失败
    String PAY_FAIL = "PAY_FAIL";
    //* PLAT_FAIL:------------平台处理失败
    String PLAT_FAIL = "PLAT_FAIL";
    // 取消支付
    String PAY_CANCEL = "PAY_CANCEL";

    String PayResult_OTHER = "OTHER";
    String REPORT_TYPE_PAY = "pay";
    String REPORT_TYPE_EXIT = "exit";


    String ACTION_GET_BANK_LIST = Constants.ACTION_PREFIX + "wallet.GETBANKLIST";

    int PAY_TYPE_BALANCE = 100;
    int PAY_TYPE_BANK = 200;

    int VOUCHER_TYPE_VALID = 0;
    int VOUCHER_TYPE_HISTORY = 1;
    int VOUCHER_STATUS_VALID = 1;
    int VOUCHER_STATUS_USED = 2;
    int VOUCHER_STATUS_EXPIRED = 3;
    String KEY_VOUCHER_TYPE = "KEY_VOUCHER_TYPE";

}

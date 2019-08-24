package com.ahdi.lib.utils.config;

public interface Constants {

    //==============================================================================================
    // -------------------------------------------通用---------------------------------------------

    String REQUEST_METHOD_GET = "GET";
    String REQUEST_METHOD_POST = "POST";
    String CHARSET_UTF_8 = "UTF-8";
    String MSG_KEY = "retMsg";
    String RET_CODE_KEY = "retCode";
    String RET_CODE_SUCCESS = "0000";
    String DATA_KEY = "Data";
    String LOCAL_RET_CODE_NETWORK_EXCEPTION = "NetWork(-1)";    // 网络异常,超时，没有应答包 等
    int HTTP_CONNECT_TIMEOUT = 10 * 1000;                       // 网络连接超时时间
    int HTTP_READ_TIMEOUT = 10 * 1000;                          // 网络数据读取超时时间

    String NOTIFY_TYPE_KEY = "notify_type";

    /**
     * 账户被挤掉
     */
    String NOTIFY_TYPE_FIVE = "5";
    /**
     * 优惠券消息
     */
    String NOTIFY_TYPE_SIX = "6";

    /**
     * 广播命名格式： 需要 action_ 开头   + ConfigCountry.SCHEMA_APP  +  其它
     */
    String ACTION_PREFIX = "action_" + ConfigCountry.SCHEMA_APP;

    /**
     * 帐号被挤掉的广播
     */
    String ACTION_ACCOUNT_OUT = ACTION_PREFIX + ".logout";

    /**
     * 消息通知的渠道id
     */
    String NOTIFICATION_CHANNEL = "notification_" + ConfigCountry.SCHEMA_APP;


    String KEY_MINUS = "-";
    String KEY_SIX_LINE = "------";
    String KEY_SIX_XING = "******";

    /**
     * 邮箱正则表达式
     */
    String EMAIL_REGEX = "^(\\w-*\\.*)+@(\\w-?)+(\\.\\w{2,})+$";
    /**
     * 相册图片以file:///开头
     */
    String START_WITH_FILE = "file:///";
    /**
     * 通用的输入框可以接受的字符
     */
    String COMMON_ACCEPT_CHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ~!@#$%^&*()_+-=;':\",./<>?`";

    String START_TOUCH_ID_LOGIN = "startTouchIDLogin";


    // -------------------------------------------通用---------------------------------------------
    //==============================================================================================


    // =============================================================================================
    // ------------------------------------------客户端定义---------------------------------------------
    String LOCAL_KEY_USERAGENT = "local_key_useragent"; //保存userAgent
    String LOCAL_FROM_KEY = "From";
    String LOCAL_PAY_CODE_NEXT_KEY = "local_pay_code_next_key";
    String LOCAL_TOKEN_KEY = "Token";

    int LOCAL_FROM_WITHDRAW = 600;  // 表示提现
    int LOCAL_FROM_PAY = 601;       // 表示支付
    int LOCAL_FROM_TOP_UP = 602;    // 表示充值

    int LOCAL_FROM_FORGOT_PWD = 603;
    int LOCAL_FROM_DELETE_BANK_CARD = 604;
    int LOCAL_FROM_MODIFY_PWD = 605;
    int LOCAL_FROM_SET_PWD = 606;

    String LOCAL_PAYMENT_PHONE = "Phone";

    int LOCAL_PAYMENT_FROM_GUIDE_SET = 101;
    int LOCAL_PAYMENT_FROM_RESET = 102;

    int TYPE_MODIFY_NICK_NAME = 1;
    int TYPE_MODIFY_EMAIL = 2;

    String LOCAL_APP_CRASH = "99999";                            // <!--Android特有，应用崩溃提示语-->
    String LOCAL_RSA_CREATE_FAIL = "93009";                      // 本地生成rsa秘钥失败
    String LOCAL_SERVER_DATA_DECODE_FAIL = "93010";              // server data 解密失败
    String LOCAL_SERVER_DATA_INVALID_FORMAT = "93011";           // server data 格式错误
    String LOCAL_SHA_EXCEPTION = "93012";                        // sha256 异常(key+支付密码版本号)
    String LOCAL_PAY_CODE_FAIL = "93013";                        // pay码生成失败
    String LOCAL_PAY_CODE_PAY_METHOD_NULL = "93014";             // pay码无可用支付方式
    String LOCAL_CAMERA_ERROR = "93015";                         // <!--Android特有，摄像头异常提示语-->


    String LOCAL_WEB_VIEW_URL_KEY = "web_view_url_key";
    String LOCAL_WEB_VIEW_TITLE_CLOSE = "web_title_close";

    String LOCAL_QRCODE_SCAN_RESULT = "qrcode_scan_result";
    /**
     * 二维码扫描之后打开webview post数据
     */
    String LOCAL_QRCODE_RESULT_POST_DATA = "qrcode_result_post_data";

    String LOCAL_POSITION_KEY = "Position";

    String LOCAL_pwd_KEY = "pwd";
    String LOCAL_random_KEY = "random";

    //BCA OTP验证码最小长度
    int LOCAL_BCA_OTP_CODE_MIN_LENGTH = 4;
    /**
     * 性别男
     */
    int LOCAL_GENDER_MALE = 1;
    /**
     * 性别女
     */
    int LOCAL_GENDER_FEMALE = 2;

    /**
     * 是否需要进行首次安装引导
     */
    String LOCAL_KEY_GUIDE_PAGER = "local_key_guide_pager";

    //英文
    String LOCAL_LAN_EN = "en";
    //印尼语
    String LOCAL_LAN_ID = "in";
    /**
     * 默认英语
     */
    String LOCAL_LAN_DEFAULT = LOCAL_LAN_EN;
    String LOCAL_LAN_KEY = "lan_key";

    String LOCAL_RECREATE_APP_MAIN_ACTIVITY = "recreate_app_main_activity";
    String LOCAL_RECREATE_SETTING_ACTIVITY = "recreate_setting_activity";

    String LOCAL_SETTING_LANGUAGE_FROM_ACTIVITY = "setting_language_from_activity";

    /**
     * 登录页面调起的设置语言
     */
    int LOCAL_LANGUAGE_FROM_LOGIN = 100;
    /**
     * 设置页面调起的设置语言
     */
    int LOCAL_LANGUAGE_FROM_SETTING = 101;

    String LOCAL_HEIGHT_KEY = "height";

    /**
     * bca相关
     */
    String LOCAL_KEY_PAY_BC_SCHEMA = "PAYBCSCHEMA";
    String LOCAL_KEY_BANK_CARD_ID = "bankID";
    String LOCAL_KEY_BCA_PAGE_TYPE = "KEY_BCA_PAGE_TYPE";
    int LOCAL_BCA_PAGE_BIND_CARD = 1;
    int LOCAL_BCA_PAGE_MODIFY_LIMIT = 2;

    //设置登录密码  0:修改密码 ,1: 重置密码,2:新建密码
    int LOCAL_INTERFACE_MODIFY_LOGIN_PWD = 0;
    int LOCAL_INTERFACE_RESET_LOGIN_PWD = 1;
    int LOCAL_INTERFACE_CREATE_LOGIN_PWD = 2;

    String LOCAL_TYPE_KEY = "Type";

    String LOCAL_NAME_KEY = "Name";

    String LOCAL_BANNER_JSON_KEY = "banner_json";

    String LOCAL_LNAME_KEY = "LName";

    String LOCAL_SLNAME_KEY = "SLName";

    String LOCAL_SNAME_KEY = "SName";

    String LOCAL_NNAME_KEY = "NName";

    String LOCAL_FEMALE_KEY = "Female";

    String LOCAL_UT_KEY = "UT";

    String LOCAL_AREA_CODE_KEY = "AreaCode";

    String LOCAL_AVATAR_KEY = "Avatar";

    String LOCAL_VOUCHER_KEY = "Voucher";

    String LOCAL_SID_KEY = "SID";

    String LOCAL_PRICE_KEY = "Price";

    String LOCAL_RESULT_KEY = "Result";

    String LOCAL_IS_OPEN_WEB_VIEW_KEY = "isOpenWebView";

    String LOCAL_ID_KEY = "Id";

    String LOCAL_FEE_KEY = "Fee";

    String LOCAL_BID_KEY = "BID";

    String LOCAL_BANK_KEY = "Bank";

    String LOCAL_PROMPT_KEY = "Prompt";

    String LOCAL_BANK_CODE_KEY = "BankCode";

    String LOCAL_TT_KEY = "TT";

    String LOCAL_TTYPE_KEY = "TType";

    String LOCAL_OT_KEY = "OT";

    String LOCAL_PAY_EX_KEY = "PayEx";

    String LOCAL_DESC_KEY = "Desc";

    String LOCAL_GNAME_KEY = "GName";

    String LOCAL_PAY_ORDER_KEY = "PayOrder";

    String LOCAL_AMOUNT_KEY = "Amount";

    String LOCAL_SUBJECT_KEY = "Subject";

    String LOCAL_OS_ANDROID = "Android";

    String LOCAL_VERIFY_CODE_KEY = "VerifyCode";

    String LOCAL_PUBLIC_KEY = "PubKey";

    String LOCAL_PRIVATE_KEY = "private_key";

    String LOCAL_BIND_ID_KEY = "BindId";

    String LOCAL_RNA_INFO_KEY = "RNAInfo";

    String LOCAL_AGREEMENTS_KEY = "Agreements";

    String LOCAL_VERSION_KEY = "Version";

    String LOCAL_TYPES_KEY = "Types";

    /**
     * 电话区号版本
     */
    String LOCAL_PHONE_AREA_KEY = "phone_area_key";
    /**
     * 电话区号数据
     */
    String LOCAL_PHONE_AREA_DATA_KEY = "phone_area_data_key";

    /**
     * app配置信息
     */
    String LOCAL_APP_CLIENT_VERSION_KEY = "local_app_client_version_key";
    String LOCAL_APP_TOP_UP_SPG_URL = "local_app_top_up_spg_url";
    String LOCAL_APP_CLIENT_HELP_URL = "local_app_client_help_url";
    String LOCAL_APP_USER_AGREEMENT_KEY = "local_app_user_agreement_key";
    String LOCAL_APP_RPWD_SERVICE_URL = "local_app_rpwd_service_url";
    String LOCAL_APP_TOUCH_AGREEMENT_URL = "local_app_touch_agreement_url";

    /**
     * 收银台以及pay码相关半弹层高度缩放
     */
    double LOCAL_DIALOG_HEIGHT_SCALE = 0.75;


    // ------------------------------------------客户端定义--------------------------------------------
    //==============================================================================================


    //==============================================================================================
    // ------------------------------------------服务端定义------------------------------------------
    /**
     * 充值结果(0:等待充值,10:充值中,20:充值失败,30:充值成功)
     */
    String APP_QUERY_PAY_RESULT_WAIT = "0";
    String APP_QUERY_PAY_RESULT_PAYING = "10";
    String APP_QUERY_PAY_RESULT_FAIL = "20";
    String APP_QUERY_PAY_RESULT_SUCCESS = "30";

    /**
     * LP - 登录名+密码
     * LV - 登录名+登录凭证
     * LS - 手机号 + 短信验证码
     * OA - OAuth登录
     */
    String LP_KEY = "LP";
    String LV_KEY = "LV";
    String LS_KEY = "LS";
    /**
     * Cm: - 普通注册
     * Ph: - 手机号注册
     * Em：- 邮箱注册
     */
    String PH_KEY = "Ph"; //手机号注册

    /**
     * UNSAFE:  非安全（未设置支付密码）;
     * SAFE:    安全（设置支付密码）
     * LOCK:    锁定（支付密码输错超过次数等）
     */
    String STATUE_UNSAFE_KEY = "UNSAFE";
    String STATUE_SAFE_KEY = "SAFE";

    String RET_CODE_5012 = "5012";      // 支付方式请求支付时支付失败pay order error

    String RET_CODE_PP003 = "PP003";    // Login terminal changed
    String RET_CODE_PP113 = "PP113";    // 登录账号被锁定

    String RET_CODE_PP001 = "PP001";    // 若用户登陆密码未被锁定，但当天累计输入错误小于5次
    String RET_CODE_PP002 = "PP002";    // 登录凭证无效
    String RET_CODE_PP004 = "PP004";    // 登录密码验证身份验证错误
    String RET_CODE_PP010 = "PP010";    // 手机号已注册
    String RET_CODE_PP013 = "PP013";    // 用户登陆密码已被锁定，则当用户在锁定限制时间内进行登录

    String RET_CODE_W000 = "W000";      // W000被踢出 session无效
    String RET_CODE_W208 = "W208";      // 非62开头的账户 调用 bankva接口

    String RET_CODE_A010 = "A010";      // 账户平台级锁定   需要特殊处理--  【A010】  转账确认页面 、添加银行账户、提现页面接口弹窗提示
    String RET_CODE_A110 = "A110";      // 已删除绑卡支付，卡支付失败
    String RET_CODE_A203 = "A203";      // 支付密码错误
    String RET_CODE_A204 = "A204";      // 支付Token过期
    String RET_CODE_A205 = "A205";      // Token失效
    String RET_CODE_A206 = "A206";      // 密码被锁定
    String RET_CODE_A207 = "A207";      // 新旧密码不能相同
    String RET_CODE_6013 = "6013";      // pay码支付超时

    String REG_KEY = "REG";

    String BCA_WIDGET_ENV_PROD = "prod";

    // 交易类型
    int TRANS_TYPE_DEFAULT = -1;
    int TRANS_TYPE_BALANCE = 10;
    int TRANS_TYPE_REFUND = 11;
    int TRANS_TYPE_TRANSFER = 20;
    int TRANS_TYPE_RECEIVE_PAY = 40;

    String PAY_AUTH_TYPE_TK = "tk";
    String PAY_AUTH_TYPE_PWD = "pwd";

    int TOUCH_FLAG_NOT_OPEN = -1;      // 用户未开启生物识别；
    int TOUCH_FLAG_NOT_CHANGE = 0;     // 生物识别版本号未变更；
    int TOUCH_FLAG_CHANGE = 1;         // 生物识别版本号已变更。

    /**
     * banner请求参数
     */
    String BANNER_REQUEST_PARAM = "P=1,2&PKGID=" + ConfigCountry.PKGID;

    // ------------------------------------------服务端定义------------------------------------------
    //==============================================================================================

}
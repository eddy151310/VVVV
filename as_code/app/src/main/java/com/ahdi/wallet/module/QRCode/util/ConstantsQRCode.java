package com.ahdi.wallet.module.QRCode.util;

/**
 * Date: 2019/6/24 下午5:52
 * Author: kay lau
 * Description:
 */
public interface ConstantsQRCode {

    /**
     * pay码初始化
     */
    String MODULE_PAY_CODE_INIT = "pay/authcode/init";

    /**
     * pay码是否使用查询
     */
    String MODULE_PAY_AUTH_CODE_QR = "pay/authcode/qr";

    /**
     * pay码支付确认
     */
    String MODULE_PAY_AUTH_CODE_CONFIRM = "pay/authcode/confirm";

    /**
     * 收款码
     */
    String MODULE_COLLECT_QR = "a/pay/coll";
}

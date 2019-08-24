package com.ahdi.wallet.cashier.util;

/**
 * Date: 2019/6/24 下午3:07
 * Author: kay lau
 * Description:
 */
public interface ConstantsCashier {

    //-----------------------payment-----------------------

    /**
     * 批价
     */
    String MODULE_PAY_PRICING = "mkt/pay/pricing";

    /**
     * 支付下单
     */
    String MODULE_PAY_ORDER = "mkt/pay/pay";

    /**
     * 查询批价
     */
    String MODULE_REPAY_CODE = "mkt/pay/repricing";

    /**
     * 支付结果查询
     */
    String MODULE_PAY_RESULT_QUERY = "mkt/pay/qr";

    //-----------------------payment-----------------------



    //-----------------------rechr-----------------------

    /**
     * 查询充值列表
     */
    String MODULE_QUERY_RECHARGE_LIST = "rechr/ls";

    /**
     * 充值下单
     */
    String MODULE_RECHARGE_ORDER = "rechr/pay";

    /**
     * 充值支付结果查询
     */
    String MODULE_QUERY_RECHARGE_PAY_RESULT = "rechr/qr/pay";

    //-----------------------rechr-----------------------
}

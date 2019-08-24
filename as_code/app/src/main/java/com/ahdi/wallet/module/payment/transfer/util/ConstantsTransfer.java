package com.ahdi.wallet.module.payment.transfer.util;


/**
 * Date: 2018/5/14 下午1:37
 * Author: kay lau
 * Description:
 */
public interface ConstantsTransfer {

    /**
     * 查询转账目标
     */
    String MODULE_TRANSFER_QUERY_TARGET = "ta/u/qr";
    /**
     * 发起转账
     */
    String MODULE_TRANSFER_CREATE = "ta/create";
    /**
     * 查询转账进度
     */
    String MODULE_TRANSFER_QUERY_PROGRESS = "mkt/ta/qr";

    /**
     * 查询最近转账联系人
     */
    String MODULE_TRANSFER_QUERY_RECENT_CONTACT = "ta/qtc";

    /**
     * 扫码转账时 获取转账对象信息  注意该接口不加    直接用api
     */
    String MODULE_GET_SCANQR_INFO = "/api/qr/transfer";
}

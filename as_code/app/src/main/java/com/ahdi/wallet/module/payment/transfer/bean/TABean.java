package com.ahdi.wallet.module.payment.transfer.bean;


import com.ahdi.lib.utils.config.ConfigCountry;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/7.
 * @email zhao_zhaohe@163.com
 * <p>
 * 转账实体
 */

public class TABean {

    private static final String TAG = TABean.class.getSimpleName();

    private String amount;//转账金额
    private String target;//根据Type类型不同  系统账户：系统账户ID  银行卡账户：账户名称+银行卡号
    private String remark;//转账备注
    private String Currency = ConfigCountry.KEY_CURRENCY;


    public TABean(String amount, String target, String remark) {
        this.amount = amount;
        this.target = target;
        this.remark = remark;
    }

    public String getAmount() {
        return amount;
    }

    public String getTarget() {
        return target;
    }

    public String getRemark() {
        return remark;
    }

    public String getCurrency() {
        return Currency;
    }
}

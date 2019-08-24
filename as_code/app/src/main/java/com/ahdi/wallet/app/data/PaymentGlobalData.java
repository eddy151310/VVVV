package com.ahdi.wallet.app.data;

import com.ahdi.wallet.app.schemas.BindCardTypeSchema;

/**
 * 全局数据
 *
 * @author zhonghu
 */
public class PaymentGlobalData {

    public final static String TAG = PaymentGlobalData.class.getSimpleName();

    private static PaymentGlobalData instance;

    public static PaymentGlobalData getInstance() {
        if (instance == null) {
            synchronized (PaymentGlobalData.class) {
                if (instance == null) {
                    instance = new PaymentGlobalData();
                }
            }
        }
        return instance;
    }


    private BindCardTypeSchema[] bindCardTypeSchemas;

    public BindCardTypeSchema[] getBindCardTypeSchemas() {
        return bindCardTypeSchemas;
    }

    public void setBindCardTypeSchemas(BindCardTypeSchema[] bindCardTypeSchemas) {
        this.bindCardTypeSchemas = bindCardTypeSchemas;
    }
}

package com.ahdi.wallet.app.main;

import com.ahdi.wallet.app.schemas.ClientCfgSchema;

public class PayConfigHelper {

    public static final String KEY_VERSION = "cfgVersion";//版本号


    private static PayConfigHelper instance = null;
    private String cfgVersion;


    public synchronized static PayConfigHelper getInstance() {
        if (instance == null) {
            instance = new PayConfigHelper();
        }
        return instance;
    }

    public PayConfigHelper() {
    }

    public String getCfgVersion() {
        return cfgVersion;
    }

    public void updateClientCfg(ClientCfgSchema clientCfg) {
        this.cfgVersion = clientCfg.cfgversion;
    }

    public void destroy() {
        instance = null;
    }
}

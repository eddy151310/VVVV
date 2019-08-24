package com.ahdi.wallet.app.main;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.wallet.cashier.response.pay.PricingResponse;
import com.ahdi.wallet.cashier.response.rechr.RechrListQueryRsp;
import com.ahdi.wallet.app.schemas.AccountSchema;
import com.ahdi.wallet.module.QRCode.schemas.AuthCodePaySchema;
import com.ahdi.wallet.app.schemas.AuthSchema;
import com.ahdi.wallet.app.schemas.ClientCfgSchema;
import com.ahdi.wallet.app.schemas.GUISchema;
import com.ahdi.wallet.cashier.schemas.MarketPayTypeSchema;
import com.ahdi.wallet.app.schemas.NotifySchema;
import com.ahdi.wallet.cashier.schemas.PayTypeSchema;
import com.ahdi.wallet.cashier.schemas.TransSchema;
import com.ahdi.wallet.app.schemas.UserSchema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class CashierPricing {

    private static CashierPricing instance = null;
    private ArrayList<PayTypeSchema> payTypesSchemaList;
    private ArrayList<MarketPayTypeSchema> marketPayTypeSchemaList;
    private ArrayList<PayTypeSchema> enablePayTypeList;
    private ArrayList<PayTypeSchema> unablePayTypeList;
    private ArrayList<GUISchema> guiSchemaList;
    private ArrayList<AuthCodePaySchema> authCodePaySchemas;
    private AuthSchema authSchema;
    private UserSchema userSchema;
    private AccountSchema accountSchema;
    private NotifySchema notifySchema;
    private TransSchema transSchema;
    private ClientCfgSchema clientCfgSchema;
    private Context context;

    public CashierPricing(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
    }

    public static CashierPricing getInstance(Context context) {
        if (instance == null) {
            synchronized (CashierPricing.class) {
                if (instance == null) {
                    instance = new CashierPricing(context);
                }
            }
        }
        return instance;
    }

    public void destroy() {
        instance = null;
    }

    public void notifyPricingResponse(PricingResponse response) {
        if (response != null) {
            if (response.getTransSchema() != null) {
                CashierPricing.getInstance(context).notifyTransSchema(response.getTransSchema());
            }
            if (response.getClientCfgSchema() != null) {
                CashierPricing.getInstance(context).updateClientCfg(response.getClientCfgSchema());
            }
            if (response.getPayTypeSchemas() != null) {
                CashierPricing.getInstance(context).notifyPayTypeSchema(response.getPayTypeSchemas());
            }
            if (response.getAuthSchema() != null) {
                CashierPricing.getInstance(context).notifyAuthSchema(response.getAuthSchema());
            }
            if (response.getUserSchema() != null) {
                CashierPricing.getInstance(context).notifyUserSchema(response.getUserSchema());
            }
            if (response.getAccountSchema() != null) {
                CashierPricing.getInstance(context).notifyAccountSchema(response.getAccountSchema());
            }
            if (response.getGuiSchemas() != null) {
                CashierPricing.getInstance(context).notifyGuiSchemas(response.getGuiSchemas());
            }
            if (response.getNotifySchema() != null) {
                CashierPricing.getInstance(context).notifyNotifySchema(response.getNotifySchema());
            }
            if (response.getMarketPayTypeSchema() != null) {
                CashierPricing.getInstance(context).notifyMPayTypeSchema(response.getMarketPayTypeSchema());
            }
        }
    }


    public void notifyQueryChargeListResponse(RechrListQueryRsp response) {
        if (response != null) {
            if (response.getPayTypeSchemas() != null) {
                notifyPayTypeSchema(response.getPayTypeSchemas());
            }
        }
    }

    /**
     * 刷新
     * 商户交易订单信息
     */
    public void notifyTransSchema(TransSchema transSchema) {
        if (transSchema != null) {
            this.transSchema = transSchema;
        }
    }

    /**
     * 刷新
     * 配置参数信息
     */
    public void updateClientCfg(ClientCfgSchema clientCfg) {
        PayConfigHelper.getInstance().updateClientCfg(clientCfg);
        if (clientCfg != null) {
            this.clientCfgSchema = clientCfg;
        }
    }

    /**
     * 刷新支付方式数据
     */
    public void notifyPayTypeSchema(PayTypeSchema[] payTypeSchemas) {
        if (payTypeSchemas == null) {
            return;
        }
        if (payTypesSchemaList != null) {
            payTypesSchemaList.clear();
        } else {
            payTypesSchemaList = new ArrayList<>();
        }

        payTypesSchemaList.addAll(Arrays.asList(payTypeSchemas));

        initPayTypesList();
    }

    /**
     * 刷新营销支付方式数据
     */
    public void notifyMPayTypeSchema(MarketPayTypeSchema[] schemas) {
        if (schemas == null) {
            return;
        }
        if (marketPayTypeSchemaList != null) {
            marketPayTypeSchemaList.clear();
        } else {
            marketPayTypeSchemaList = new ArrayList<>();
        }
        marketPayTypeSchemaList.addAll(Arrays.asList(schemas));
    }

    public void notifyAuthSchema(AuthSchema authSchema) {
        if (authSchema != null) {
            this.authSchema = authSchema;
        }
    }

    public void notifyUserSchema(UserSchema userSchema) {
        if (userSchema != null) {
            this.userSchema = userSchema;
        }
    }

    public void notifyAccountSchema(AccountSchema accountSchema) {
        if (accountSchema != null) {
            this.accountSchema = accountSchema;
        }
    }

    private void notifyGuiSchemas(GUISchema[] guiSchemas) {
        if (guiSchemas == null) {
            return;
        }
        if (guiSchemaList != null) {
            guiSchemaList.clear();
        } else {
            guiSchemaList = new ArrayList<>();
        }
        for (int i = 0; i < guiSchemas.length; i++) {
            GUISchema schema = guiSchemas[i];
            guiSchemaList.add(schema);
        }
    }

    public void notifyNotifySchema(NotifySchema notifySchema) {
        if (notifySchema != null) {
            this.notifySchema = notifySchema;
        }
    }

    public void notifyAuthCodePaySchemas(AuthCodePaySchema[] schemas) {
        if (schemas == null) {
            return;
        }
        if (authCodePaySchemas != null) {
            authCodePaySchemas.clear();
        } else {
            authCodePaySchemas = new ArrayList<>();
        }
        Collections.addAll(authCodePaySchemas, schemas);
    }


    public AuthSchema getAuthSchema() {
        return authSchema;
    }

    public UserSchema getUserSchema() {
        return userSchema;
    }

    public AccountSchema getAccountSchema() {
        return accountSchema;
    }

    public NotifySchema getNotifySchema() {
        return notifySchema;
    }

    public TransSchema getTransSchema() {
        return transSchema;
    }

    public ClientCfgSchema getClientCfgSchema() {
        return clientCfgSchema;
    }

    public ArrayList<PayTypeSchema> getPayTypesSchemaList() {
        if (payTypesSchemaList == null) {
            payTypesSchemaList = new ArrayList<>();
        }
        return payTypesSchemaList;
    }

    public ArrayList<MarketPayTypeSchema> getMarketPayTypeSchemaList() {
        if (marketPayTypeSchemaList == null) {
            marketPayTypeSchemaList = new ArrayList<>();
        }
        return marketPayTypeSchemaList;
    }

    public ArrayList<GUISchema> getGUISchemaList() {
        if (guiSchemaList == null) {
            guiSchemaList = new ArrayList<>();
        }
        return guiSchemaList;
    }

    public void initPayTypesList() {
        if (enablePayTypeList != null) {
            enablePayTypeList.clear();
        } else {
            enablePayTypeList = new ArrayList<>();
        }
        if (unablePayTypeList != null) {
            unablePayTypeList.clear();
        } else {
            unablePayTypeList = new ArrayList<>();
        }
        for (int i = 0; i < payTypesSchemaList.size(); i++) {
            if (TextUtils.isEmpty(payTypesSchemaList.get(i).Block)) {
                enablePayTypeList.add(payTypesSchemaList.get(i));
            } else {
                unablePayTypeList.add(payTypesSchemaList.get(i));
            }
        }
    }

    public ArrayList<PayTypeSchema> getEnablePayTypes() {
        if (enablePayTypeList == null) {
            enablePayTypeList = new ArrayList<>();
        }
        return enablePayTypeList;
    }

    public ArrayList<PayTypeSchema> getUnablePayTypes() {
        if (unablePayTypeList == null) {
            unablePayTypeList = new ArrayList<>();
        }
        return unablePayTypeList;
    }

    public ArrayList<AuthCodePaySchema> getAuthCodePaySchemas() {
        if (authCodePaySchemas == null) {
            authCodePaySchemas = new ArrayList<>();
        }
        return authCodePaySchemas;
    }

    public String getTT() {
        if (transSchema == null) {
            return null;
        }
        return transSchema.TT;
    }
}

package com.ahdi.wallet.cashier.response.pay;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.AccountSchema;
import com.ahdi.wallet.app.schemas.AuthSchema;
import com.ahdi.wallet.app.schemas.ClientCfgSchema;
import com.ahdi.wallet.app.schemas.GUISchema;
import com.ahdi.wallet.cashier.schemas.MarketPayTypeSchema;
import com.ahdi.wallet.app.schemas.NotifySchema;
import com.ahdi.wallet.cashier.schemas.PayTypeSchema;
import com.ahdi.wallet.cashier.schemas.TransSchema;
import com.ahdi.wallet.app.schemas.UserSchema;

import org.json.JSONObject;

public class PricingResponse extends Response {

    private TransSchema transSchema;
    private ClientCfgSchema clientCfgSchema;//Map
    private PayTypeSchema[] payTypeSchemas;
    private AuthSchema authSchema;
    private UserSchema userSchema;
    private AccountSchema accountSchema;
    private GUISchema[] guiSchemas;
    private NotifySchema notifySchema;//跑马灯数据，扩展字段E，第二位1
    /**
     * 生物识别版本检查结果标识。
     * -1 - 用户未开启生物识别；
     * 0 - 生物识别版本号未变更；
     * 1 - 生物识别版本号已变更。
     * 当TouchVer非空时，有该值。
     */
    public int touchFlag;
    private MarketPayTypeSchema[] marketPayTypeSchemas; // 营销支付方式信息


    public PricingResponse() {
        super();
    }

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        transSchema = ABSIO.decodeSchema(TransSchema.class, body.optJSONObject("Trans"));
        clientCfgSchema = ABSIO.decodeSchema(ClientCfgSchema.class, body.optJSONObject("ClientCfg"));
        payTypeSchemas = ABSIO.decodeSchemaArray(PayTypeSchema.class, "PayTypes", body);
        authSchema = ABSIO.decodeSchema(AuthSchema.class, body.optJSONObject("Auth"));
        userSchema = ABSIO.decodeSchema(UserSchema.class, body.optJSONObject("User"));
        accountSchema = ABSIO.decodeSchema(AccountSchema.class, body.optJSONObject("Account"));
        guiSchemas = ABSIO.decodeSchemaArray(GUISchema.class, "GUI", body);
        notifySchema = ABSIO.decodeSchema(NotifySchema.class, body.optJSONObject("Notify"));
        touchFlag = body.optInt("TouchFlag", -100);
        marketPayTypeSchemas = ABSIO.decodeSchemaArray(MarketPayTypeSchema.class, "MPayTypes", body);

    }

    public TransSchema getTransSchema() {
        return transSchema;
    }

    public ClientCfgSchema getClientCfgSchema() {
        return clientCfgSchema;
    }

    public PayTypeSchema[] getPayTypeSchemas() {
        return payTypeSchemas;
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

    public GUISchema[] getGuiSchemas() {
        return guiSchemas;
    }

    public NotifySchema getNotifySchema() {
        return notifySchema;
    }

    public MarketPayTypeSchema[] getMarketPayTypeSchema() {
        return marketPayTypeSchemas;
    }
}

package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.AccountSchema;

import org.json.JSONObject;

/**
 * Date: 2017/10/14 下午3:16
 * Author: kay lau
 * Description:
 */
public class QueryBalanceRsp extends Response {

    private AccountSchema accountSchema;

    /**
     * 提现开关：
     * 0- 关闭提现，1-开启提现
     * true:  开启提现
     * false: 关闭提现(默认)
     */
    public boolean isWithdraw;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        accountSchema = ABSIO.decodeSchema(AccountSchema.class, body.optJSONObject("Account"));
        int withdrawSW = body.optInt("WithdrawSW");
        isWithdraw = withdrawSW == 1;
    }

    public AccountSchema getAccountSchema() {
        return accountSchema;
    }
}

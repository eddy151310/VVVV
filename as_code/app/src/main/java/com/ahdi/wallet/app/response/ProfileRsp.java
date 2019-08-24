package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.AccountSchema;
import com.ahdi.wallet.app.schemas.UserSchema;

import org.json.JSONObject;

/**
 * Date: 2017/10/14 下午3:16
 * Author: kay lau
 * Description:
 */
public class ProfileRsp extends Response {

//    字段名	重要性	类型	    描述
//    User	    必须	User schema 用户账号信息
//    Account	必须	Account_schema 用户账户信息
//    CardNum	必须	int 	用户银行卡数量
//    HasMsg	必须	int 	用户新消息：0无；1有

    private UserSchema userSchema;
    private AccountSchema accountSchema;
    private int cardNum;
    private int accNum;
    private boolean hasMsg;

    /**
     * 是否需要实名认证
     * 实名认证开关：
     * 0- 不需要认证，1-需要认证
     * true:  需要认证
     * false: 不需要认证(默认)
     */
    public boolean isRnaSw;

    /**
     * 提现开关：
     * 0- 关闭提现，1-开启提现
     * true:  开启提现
     * false: 关闭提现(默认)
     */
    public boolean isWithdraw;
    /**
     * 是否需要实名认证：
     * 0-不需要认证，1-需要认证
     * true:  需要认证
     * false: 不需要认证(默认)
     */
    public boolean isNeedRNA;

    public boolean hasPwd;//是否有登录密码

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }
        userSchema = ABSIO.decodeSchema(UserSchema.class, body.optJSONObject("User"));
        accountSchema = ABSIO.decodeSchema(AccountSchema.class, body.optJSONObject("Account"));
        cardNum = body.optInt("CardNum");
        accNum = body.optInt("AccNum");
        if (body.optInt("HasMsg") == 1) {
            hasMsg = true;
        }
        isRnaSw = body.optInt("RNASW") == 1;
        isWithdraw = body.optInt("WithdrawSW") == 1;
        isNeedRNA = body.optInt("IsNeedRNA") == 1;
        hasPwd = body.optInt("HasLPwd") == 1;
    }

    public UserSchema getUserSchema() {
        return userSchema;
    }

    public AccountSchema getAccountSchema() {
        return accountSchema;
    }

    public int getCardNum() {
        return cardNum;
    }

    public boolean isHasMsg() {
        return hasMsg;
    }

    public int getAccNum() {
        return accNum;
    }

}

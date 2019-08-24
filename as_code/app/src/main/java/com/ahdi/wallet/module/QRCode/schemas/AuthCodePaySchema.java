package com.ahdi.wallet.module.QRCode.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2018/5/18 下午7:46
 * Author: kay lau
 * Description:
 */
public class AuthCodePaySchema extends ABSIO {

    /**
     * UNSAFE: 非安全（未设置支付密码）;
     * SAFE:安全（设置支付密码）
     * LOCK:锁定（支付密码输错超过次数等）
     */
    public long BindId;

    public String Bank;

    public String BankIcon;

    public String FeeDesc;      // 单笔交易手续费显示文案

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        BindId = json.optLong("BindId");
        Bank = json.optString("Bank");
        BankIcon = json.optString("Icon");
        FeeDesc = json.optString("FeeDesc");

    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

}

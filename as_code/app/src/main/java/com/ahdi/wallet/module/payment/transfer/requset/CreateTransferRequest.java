package com.ahdi.wallet.module.payment.transfer.requset;

import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.module.payment.transfer.bean.TABean;
import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.wallet.app.schemas.TerminalInfoSchema;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/7.
 * @email zhao_zhaohe@163.com
 * <p>
 * 创建转账
 */

public class CreateTransferRequest extends Request {

    //    字段名	    重要性	类型  	                描述
    //    TInfo	    必须	    TerminalInfo_Schema	    终端信息列表
    //    TA.Amount	必须  	String	                转账金额
    //    TA.Type	必须	    int	                    0 – 转给系统账户 1 – 转给银行卡账户
    //    TA.Target	必须	    String              	根据Type类型不同  系统账户：系统账户ID  银行卡账户：账户名称+银行卡号
    //    TA.Rmark	必须	    String	                转账备注

    private static final String TAG = CreateTransferRequest.class.getSimpleName();

    private TerminalInfoSchema terminalInfo = new TerminalInfoSchema();
    private TABean taBean;

    public CreateTransferRequest(TABean taBean, String sid) {
        super(sid);
        this.taBean = taBean;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            body = terminalInfo.writeTo(body); //添加 terminalInfo
            if (taBean != null) {
                body.put("Amount", taBean.getAmount());
                body.put("Currency", taBean.getCurrency());
                body.put("Target", taBean.getTarget());
                body.put("Remark", taBean.getRemark());
            }

            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, json.toString());
        return json;
    }
}

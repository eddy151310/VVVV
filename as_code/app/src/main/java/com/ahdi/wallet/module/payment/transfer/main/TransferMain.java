package com.ahdi.wallet.module.payment.transfer.main;

import android.content.Context;

import com.ahdi.wallet.R;
import com.ahdi.wallet.cashier.PayCashierSdk;
import com.ahdi.wallet.module.payment.transfer.bean.TABean;
import com.ahdi.wallet.module.payment.transfer.callback.TransferResultCallBack;
import com.ahdi.wallet.module.payment.transfer.listener.CreateTransferListener;
import com.ahdi.wallet.module.payment.transfer.listener.PayListener;
import com.ahdi.wallet.module.payment.transfer.listener.QueryQRTargetListener;
import com.ahdi.wallet.module.payment.transfer.listener.QueryTargetListener;
import com.ahdi.wallet.module.payment.transfer.listener.QueryTransferContactListener;
import com.ahdi.wallet.module.payment.transfer.listener.QueryTransferProgressListener;
import com.ahdi.wallet.module.payment.transfer.requset.CreateTransferRequest;
import com.ahdi.wallet.module.payment.transfer.requset.QueryQRTargetRequest;
import com.ahdi.wallet.module.payment.transfer.requset.QueryRecentTransContactsRequest;
import com.ahdi.wallet.module.payment.transfer.requset.QueryTargetRequest;
import com.ahdi.wallet.module.payment.transfer.requset.QueryTransferProgressRequest;

import org.json.JSONObject;

/**
 * @author admin
 * 转账逻辑处理类
 */

public class TransferMain {

    private static TransferMain instance;
    /**
     * 对外的结果回调
     */
    public TransferResultCallBack callBack;
    /**
     * 默认的错误原因
     */
    public String default_error = "";
    private Context context;


    public static TransferMain getInstance() {
        if (instance == null) {
            synchronized (TransferMain.class) {
                if (instance == null) {
                    instance = new TransferMain();
                }
            }
        }
        return instance;
    }

    private TransferMain() {
    }

    /**
     * 初始化默认的错误原因
     *
     * @param context
     */
    private void initDefaultError(Context context) {
        if (context != null) {
            default_error = context.getString(R.string.LocalError_C0);
        }
    }

    /**
     * 创建转账
     *
     * @param context
     * @param bean
     * @param sid
     */
    public void createTransfer(Context context, TABean bean, String sid) {
        initDefaultError(context);
        this.context = context;
        CreateTransferRequest request = new CreateTransferRequest(bean, sid);
        HttpReqTransfer.getInstance().onCreateTransfer(request, new CreateTransferListener(sid));
    }

    /**
     * 支付
     *
     * @param id
     * @param pay
     * @param sid
     */
    public void pay(String id, String pay, String sid, int from) {
        PayCashierSdk.startPay(context, pay, sid, from, new PayListener(id, from));
    }

    /**
     * 查询转账目标
     *
     * @param context
     * @param lName
     * @param sid
     */
    public void queryTransferTarget(Context context, String lName, String sid) {
        initDefaultError(context);
        QueryTargetRequest request = new QueryTargetRequest(lName, sid);
        HttpReqTransfer.getInstance().queryTransferTarget(request, new QueryTargetListener());
    }

    /**
     * 查询二维码扫描出来的转账目标  get请求
     *
     * @param context
     */
    public void queryQRTransferTarget(Context context, String param) {
        initDefaultError(context);
        QueryQRTargetRequest request = new QueryQRTargetRequest(param);
        HttpReqTransfer.getInstance().queryQRTransferTarget(request, new QueryQRTargetListener());
    }

    /**
     * 查询转账进度
     *
     * @param context
     * @param id
     * @param sid
     */
    public void queryTransferProgress(Context context, String id, String sid) {
        initDefaultError(context);
        QueryTransferProgressRequest request = new QueryTransferProgressRequest(id, sid);
        HttpReqTransfer.getInstance().queryTransferProgress(request, new QueryTransferProgressListener(id));
    }

    /**
     * 2.2.1.4查询最近转账联系人
     *
     * @param context
     * @param sid
     * @param callBack
     */
    public void queryRecentTransContact(Context context, String sid, TransferResultCallBack callBack) {
        initDefaultError(context);
        QueryRecentTransContactsRequest request = new QueryRecentTransContactsRequest(sid);
        HttpReqTransfer.getInstance().queryRecentTransContact(request, new QueryTransferContactListener(callBack));
    }

    /**
     * 统一对外回调
     *
     * @param code       “0”成功 其他失败
     * @param errorMsg   错误描述
     * @param jsonObject 响应成功时的json数据
     */
    public void onResultBack(String code, String errorMsg, JSONObject jsonObject, String ID) {
        if (callBack != null) {
            callBack.onResult(code, errorMsg, jsonObject, ID);
        }
        onDestroy();
    }

    /**
     * 清理
     */
    private void onDestroy() {
        callBack = null;
        instance = null;
        context = null;
    }
}

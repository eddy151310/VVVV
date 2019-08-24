package com.ahdi.wallet.module.payment.transfer.main;

import android.os.AsyncTask;

import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.wallet.module.payment.transfer.requset.CreateTransferRequest;
import com.ahdi.wallet.module.payment.transfer.requset.QueryQRTargetRequest;
import com.ahdi.wallet.module.payment.transfer.requset.QueryRecentTransContactsRequest;
import com.ahdi.wallet.module.payment.transfer.requset.QueryTargetRequest;
import com.ahdi.wallet.module.payment.transfer.requset.QueryTransferProgressRequest;
import com.ahdi.wallet.module.payment.transfer.util.ConstantsTransfer;
import com.ahdi.wallet.network.HttpReqAsyncTask;

public class HttpReqTransfer {

    private static HttpReqTransfer instance;

    private HttpReqTransfer() {

    }

    public synchronized static HttpReqTransfer getInstance() {
        if (instance == null) {
            instance = new HttpReqTransfer();
        }
        return instance;
    }

    /**
     * 2.2.1.1	查询转账目标
     *
     * @param request
     * @param listener
     */
    public void queryTransferTarget(QueryTargetRequest request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsTransfer.MODULE_TRANSFER_QUERY_TARGET, request, listener)).execute();
    }

    /**
     * 2.2.1.1	查询二维码转账目标  get请求
     *
     * @param request
     * @param listener
     */
    public void queryQRTransferTarget(QueryQRTargetRequest request, HttpReqTaskListener listener) {
        HttpReqAsyncTask.Param param = new HttpReqAsyncTask.Param(ConstantsTransfer.MODULE_GET_SCANQR_INFO, request, listener);
        param.isGetMethod = true;
        new HttpReqAsyncTask(param).execute();
    }

    /**
     * 2.2.1.2	发起转账
     *
     * @param request
     * @param listener
     */
    public void onCreateTransfer(CreateTransferRequest request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsTransfer.MODULE_TRANSFER_CREATE, request, listener)).execute();
    }

    /**
     * 查询转账进度
     *
     * @param request
     * @param listener
     */
    public void queryTransferProgress(QueryTransferProgressRequest request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsTransfer.MODULE_TRANSFER_QUERY_PROGRESS, request, listener)).execute();
    }

    /**
     * 查询最近转账联系人
     *
     * @param request
     * @param listener
     */
    public void queryRecentTransContact(QueryRecentTransContactsRequest request, HttpReqTaskListener listener) {
        new HttpReqAsyncTask(new HttpReqAsyncTask.Param(ConstantsTransfer.MODULE_TRANSFER_QUERY_RECENT_CONTACT, request, listener)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}

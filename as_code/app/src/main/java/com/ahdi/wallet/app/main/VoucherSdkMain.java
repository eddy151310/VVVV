package com.ahdi.wallet.app.main;

import android.content.Context;
import android.os.AsyncTask;

import com.ahdi.wallet.app.HttpReqApp;
import com.ahdi.wallet.app.callback.VoucherSdkCallBack;
import com.ahdi.wallet.app.listener.voucher.VoucherDetailListener;
import com.ahdi.wallet.app.listener.voucher.VoucherListListener;
import com.ahdi.wallet.app.request.VoucherDetailReq;
import com.ahdi.wallet.app.request.VoucherListReq;

/**
 * @author xiaoniu
 * 优惠券main
 */
public class VoucherSdkMain {

    private static VoucherSdkMain instance;
    /**
     * 默认的错误原因
     */
    public String default_error = "";

    private VoucherSdkMain() {
    }

    public static VoucherSdkMain getInstance() {
        if (instance == null) {
            synchronized (VoucherSdkMain.class) {
                if (instance == null) {
                    instance = new VoucherSdkMain();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化默认的错误原因
     *
     * @param context
     */
    private void initDefaultError(Context context) {
        if (context != null) {
            default_error = context.getString(com.ahdi.lib.utils.R.string.LocalError_C0);
        }
    }

    /**
     * 获取优惠券列表
     * @param context
     * @param sid
     * @param id
     * @param cType
     * @param callBack
     */
    public AsyncTask voucherList(Context context, String sid, String id, int cType, VoucherSdkCallBack callBack) {
        initDefaultError(context);
        VoucherListReq request = new VoucherListReq(sid, id, cType);
        return  HttpReqApp.getInstance().onVoucherList(request, new VoucherListListener(callBack));
    }

    /**
     * 获取优惠券列表
     * @param context
     * @param sid
     * @param id
     * @param callBack
     */
    public void voucherDetail(Context context, String sid, String id, VoucherSdkCallBack callBack) {
        initDefaultError(context);
        VoucherDetailReq request = new VoucherDetailReq(sid, id);
        HttpReqApp.getInstance().onVoucherDetail(request, new VoucherDetailListener(callBack));
    }
}
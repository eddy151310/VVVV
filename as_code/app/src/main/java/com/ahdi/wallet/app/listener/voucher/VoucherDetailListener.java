package com.ahdi.wallet.app.listener.voucher;

import com.ahdi.wallet.app.response.VoucherDetailRsp;
import com.ahdi.wallet.app.sdk.VoucherSdk;
import com.ahdi.wallet.app.callback.VoucherSdkCallBack;
import com.ahdi.wallet.app.main.VoucherSdkMain;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

/**
 * 查询优惠券详情
 *
 * @author zhaohe
 */
public class VoucherDetailListener implements HttpReqTaskListener {

    private static final String TAG = "VoucherDetailListener";

    private VoucherSdkCallBack callBack;

    public VoucherDetailListener(VoucherSdkCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e("VoucherDetailRsp:",json.toString());
        }
        VoucherDetailRsp resp = VoucherDetailRsp.decodeJson(VoucherDetailRsp.class, json);
        if (resp != null) {
            onResult(resp.getmHeader().RetCode, resp.getmHeader().ErrMsg, resp);
        } else {
            onResult(VoucherSdk.LOCAL_SYSTEM_EXCEPTION, VoucherSdkMain.getInstance().default_error, null);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? "VoucherListListener--onError(JSONObject e)" : json.toString());
        onResult(VoucherSdk.LOCAL_NETWORK_EXCEPTION, json.optString(Constants.MSG_KEY), null);
    }

    /**
     * 回调
     * @param code
     * @param errorMsg
     * @param response
     */
    private void onResult(String code, String errorMsg, Response response){
        if (callBack != null){
            callBack.onResult(code, errorMsg, response);
        }
    }
}

package com.ahdi.wallet.app.listener.account;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.sdk.BankCardSdk;
import com.ahdi.wallet.bca.BcaSdk;
import com.ahdi.wallet.bca.callback.BcaBankCardCallBack;
import com.ahdi.wallet.app.main.BankCardSdkMain;
import com.ahdi.wallet.app.response.SetCardLimitRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;

import org.json.JSONObject;

/**
 * 修改绑卡限额
 */
public class SetBindCardLimitListener implements HttpReqTaskListener {

    private static final String TAG = SetBindCardLimitListener.class.getSimpleName();

    private Context context;
    private String sid;
    private String bid;

    public SetBindCardLimitListener(Context context, String sid, String bid) {
        this.context = context;
        this.sid = sid;
        this.bid = bid;
    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        SetCardLimitRsp resp = SetCardLimitRsp.decodeJson(SetCardLimitRsp.class, json);
        if (resp != null) {
            if (BankCardSdk.LOCAL_PAY_SUCCESS.equals(resp.getmHeader().retCode)) {
                String invoke = resp.getPayBCBindSchema().getInvoke();
                if (invoke.equals("SDK")) {
                    //吊起BCA修改限额
                    BcaSdk.bcaLimit(context, sid, bid, resp.getPayBCBindSchema(), new BcaBankCardCallBack() {
                        @Override
                        public void onSuccess() {
                            //BCA修改限额成功，停留在管理卡详情刷新
                            BankCardSdkMain.getInstance().onResultBack(BankCardSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().retMsg, json);
                        }

                        @Override
                        public void onFail(String msg) {
                            //绑卡取消，停留在当前页面
                            BankCardSdkMain.getInstance().onResultBack(BankCardSdk.LOCAL_PAY_UP_CANCEL, resp.getmHeader().retMsg, json);
                        }
                    });
                }

            } else {
                checkErrorResult(resp.getmHeader().retCode, resp.getmHeader().retMsg);
            }
        } else {
            BankCardSdkMain.getInstance().onResultBack(BankCardSdk.LOCAL_PAY_SYSTEM_EXCEPTION, BankCardSdkMain.getInstance().default_error, null);
            LogUtil.d(TAG, "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        BankCardSdkMain.getInstance().onResultBack(BankCardSdk.LOCAL_PAY_NETWORK_EXCEPTION, "", json);
    }

    public void showDialog(String btnText, String msg) {
        new CommonDialog
                .Builder(context)
                .setMessage(msg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }


    public void showControlDialog(String msg) {
        new CommonDialog
                .Builder(context)
                .setMessage(msg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.DialogButton_B0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void checkErrorResult(String code, String errorMsg) {
        if (code.equals(Constants.RET_CODE_A203)) {
            showDialog(context.getString(R.string.DialogButton_D0), errorMsg);
        } else if (TextUtils.equals(code, Constants.RET_CODE_A010)) {
            showDialog(context.getString(R.string.DialogButton_B0), errorMsg);
        } else if (code.equals(Constants.RET_CODE_A206)) {
            showControlDialog(errorMsg);
        } else {
            ToastUtil.showToastLong(context, errorMsg);
        }
    }
}

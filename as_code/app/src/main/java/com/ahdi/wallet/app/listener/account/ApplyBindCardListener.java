package com.ahdi.wallet.app.listener.account;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.sdk.BankCardSdk;
import com.ahdi.wallet.bca.BcaSdk;
import com.ahdi.wallet.bca.callback.BcaBankCardCallBack;
import com.ahdi.wallet.app.main.BankCardSdkMain;
import com.ahdi.wallet.app.response.ApplyBindCardRsp;
import com.ahdi.wallet.app.ui.activities.bankCard.BankCardPayWebActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;

import org.json.JSONObject;

public class ApplyBindCardListener implements HttpReqTaskListener {

    private static final String TAG = ApplyBindCardListener.class.getSimpleName();

    private Activity context;
    private String sid;
    private LoadingDialog loadingDialog;

    public ApplyBindCardListener(Activity context, String sid, LoadingDialog loadingDialog) {
        this.context = context;
        this.sid = sid;
        this.loadingDialog = loadingDialog;
    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        LoadingDialog.dismissDialog(loadingDialog);
        ApplyBindCardRsp resp = ApplyBindCardRsp.decodeJson(ApplyBindCardRsp.class, json);
        if (resp != null) {
            if (BankCardSdk.LOCAL_PAY_SUCCESS.equals(resp.getmHeader().retCode)) {
                String invoke = resp.getPayBCBindSchema().getInvoke();
                if (invoke.equals("WEB")) {
                    String url = resp.getPayBCBindSchema().getUrl();
                    if (!TextUtils.isEmpty(url)) {
                        showBankWebView(url);
                    }
                } else {
                    //吊起BCA界面绑卡
                    BcaSdk.bcaBindCard(context, sid, resp.getPayBCBindSchema(), new BcaBankCardCallBack() {
                        @Override
                        public void onSuccess() {
                            //BCA绑卡成功，去刷新成功列表
                            BankCardSdkMain.getInstance().onResultBack(BankCardSdk.LOCAL_PAY_SUCCESS, resp.getmHeader().retMsg, json);
                            context.finish();
                        }

                        @Override
                        public void onFail(String msg) {
                            //绑卡取消或失败都停留在当前页面
                            LogUtil.d(TAG, "--绑卡步骤中断---");
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
        LoadingDialog.dismissDialog(loadingDialog);
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        BankCardSdkMain.getInstance().onResultBack(BankCardSdk.LOCAL_PAY_NETWORK_EXCEPTION, "", json);
    }

    public void showBankWebView(String url) {
        if (context != null) {
            Intent intent = new Intent(context, BankCardPayWebActivity.class);
            intent.putExtra(Constants.LOCAL_WEB_VIEW_URL_KEY, url);
            intent.putExtra(Constants.SP_KEY_SID, sid);
            context.startActivity(intent);
        }
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

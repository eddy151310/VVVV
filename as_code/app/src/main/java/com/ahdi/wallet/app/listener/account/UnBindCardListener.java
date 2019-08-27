package com.ahdi.wallet.app.listener.account;

import android.app.Activity;
import android.content.DialogInterface;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.sdk.BankCardSdk;
import com.ahdi.wallet.app.main.BankCardSdkMain;
import com.ahdi.wallet.app.response.UnBindCardRsp;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.network.HttpReqTaskListener;

import org.json.JSONObject;

/**
 * Date: 2018/1/5 下午5:09
 * Author: kay lau
 * Description:
 */
public class UnBindCardListener implements HttpReqTaskListener {

    private static final String TAG = UnBindCardListener.class.getSimpleName();

    private Activity context;
    private LoadingDialog loadingDialog;

    public UnBindCardListener(Activity context, LoadingDialog loadingDialog) {
        this.context = context;
        this.loadingDialog = loadingDialog;
    }

    @Override
    public void onPostExecute(JSONObject json) {
        if (json != null) {
            LogUtil.e(TAG, json.toString());
        }
        LoadingDialog.dismissDialog(loadingDialog);
        UnBindCardRsp resp = UnBindCardRsp.decodeJson(UnBindCardRsp.class, json);
        if (resp != null) {
            if (BankCardSdk.LOCAL_PAY_SUCCESS.equals(resp.getmHeader().retCode)) {
                BankCardSdkMain.getInstance().onResultBack(resp.getmHeader().retCode, resp.getmHeader().retMsg, json);
                context.finish();
            } else {
                checkErrorResult(resp.getmHeader().retCode, resp.getmHeader().retMsg);
            }
        } else {
            BankCardSdkMain.getInstance().onResultBack(BankCardSdk.LOCAL_PAY_SYSTEM_EXCEPTION, BankCardSdkMain.getInstance().default_error, null);
            LogUtil.d(TAG, TAG + "解析之后为空");
        }
    }

    @Override
    public void onError(JSONObject json) {
        LoadingDialog.dismissDialog(loadingDialog);
        LogUtil.e(TAG, json == null ? TAG + "onError(JSONObject e)" : json.toString());
        BankCardSdkMain.getInstance().onResultBack(BankCardSdk.LOCAL_PAY_NETWORK_EXCEPTION, "", json);
    }

    public void showDialog(String msg) {
        new CommonDialog
                .Builder(context)
                .setMessage(msg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.DialogButton_D0), new DialogInterface.OnClickListener() {
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
            showDialog(errorMsg);
        } else if (code.equals(Constants.RET_CODE_A206)) {
            showControlDialog(errorMsg);
        } else {
            ToastUtil.showToastLong(context, errorMsg);
        }
    }
}

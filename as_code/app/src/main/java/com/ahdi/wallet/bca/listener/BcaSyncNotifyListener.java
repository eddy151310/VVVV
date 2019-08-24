package com.ahdi.wallet.bca.listener;

import android.app.Activity;
import android.content.DialogInterface;

import com.ahdi.wallet.R;
import com.ahdi.wallet.bca.main.BcaSdkMain;
import com.ahdi.wallet.bca.response.BcaResultNotifyRsp;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;

import org.json.JSONObject;

/**
 * @author xiaoniu
 */
public class BcaSyncNotifyListener implements HttpReqTaskListener {

    private LoadingDialog loadingDialog;

    private Activity activity;
    private String init;
    private String method;
    private String data;
    private String bid;

    public BcaSyncNotifyListener(Activity activity, String init, String method, String data, String bid) {
        this.activity = activity;
        this.init = init;
        this.method = method;
        this.data = data;
        this.bid = bid;
        loadingDialog = LoadingDialog.showDialogLoading(activity,activity.getString(R.string.DialogTitle_C0));
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(JSONObject json) {
        closeLoading();
        BcaResultNotifyRsp response = BcaResultNotifyRsp.decodeJson(BcaResultNotifyRsp.class,json);
        if (response != null && response.getmHeader().RetCode.equals(Constants.RET_CODE_SUCCESS)){
            activity.finish();
            BcaSdkMain.getInstance().onBankCardResultSuccess();
        }else {
            //绑卡结果通知接口失败，弹层提示用户重试，或取消
            showDialogToAgainNotify(activity,response.getmHeader().ErrMsg,init,method,data,bid);
        }
    }

    @Override
    public void onError(JSONObject json) {
        closeLoading();
        showDialogToAgainNotify(activity,json.optString(Constants.MSG_KEY),init,method,data,bid);
    }


    private void closeLoading(){
        if (loadingDialog != null) {
            LoadingDialog.dismissDialog(loadingDialog);
        }
    }

    /**
     * 弹窗重试上报绑卡通知或取消
     * @param activity
     */
    private void showDialogToAgainNotify(final Activity activity,String msg, final String init, final String method, final String data, final String bid){
        new CommonDialog
                .Builder(activity)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.DialogButton_C0), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BcaSdkMain.getInstance().bcaResultSyncNotify(activity,init,method,data,bid);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(activity.getString(R.string.DialogButton_A0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        activity.finish();
                        BcaSdkMain.getInstance().bcaResultAsyncNotify(init,method,data,bid);
                        BcaSdkMain.getInstance().onBankCardResultSuccess();
                    }
                })
                .show();
    }

}

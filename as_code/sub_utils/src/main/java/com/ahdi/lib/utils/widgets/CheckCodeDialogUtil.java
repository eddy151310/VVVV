package com.ahdi.lib.utils.widgets;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import com.ahdi.lib.utils.R;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;

/**
 * Date: 2018/4/20 下午7:20
 * Author: kay lau
 * Description:
 */
public class CheckCodeDialogUtil {

    public static final String VERIFICATION_FAILED = "1002";
    public static final String VERIFICATION_FAILED_ABOVE_NORM = "1003";
    public static final String VERIFY_CODE_ERROR = "1004";

    private static CheckCodeDialogUtil instance;


    private CheckCodeDialogUtil() {
    }

    public synchronized static CheckCodeDialogUtil getInstance() {
        if (instance == null) {
            instance = new CheckCodeDialogUtil();
        }
        return instance;
    }

    public void showDialog(Context context, String errorMsg, String errorCode) {
        showDialog(context, errorMsg, errorCode, "");
    }

    public void showDialog(Context context, String errorMsg, String errorCode, String loginName) {
        DialogInterface.OnClickListener positiveListener = null;
        DialogInterface.OnClickListener negativeListener = null;
        DialogData dialogData = new DialogData();
        dialogData.errorMsg = errorMsg;
        switch (errorCode) {
            case Constants.RET_CODE_SUCCESS:
                dialogData.positiveButtonText = context.getString(R.string.DialogButton_B0);
                positiveListener = getPositiveListener();
                break;
            case Constants.RET_CODE_PP001:
            case Constants.RET_CODE_PP004:
            case VERIFICATION_FAILED:
                dialogData.positiveButtonText = context.getString(R.string.DialogButton_C0);
                positiveListener = getPositiveListener();
                if (loginName.startsWith(ConfigCountry.KEY_AREA_CODE)) {
                    dialogData.negativeButtonText = context.getString(R.string.DialogButton_F0);
                    negativeListener = getNegativeListener(context, loginName);
                }
                break;
            case Constants.RET_CODE_PP013:
            case VERIFICATION_FAILED_ABOVE_NORM:
                dialogData.positiveButtonText = context.getString(R.string.DialogButton_B0);
                positiveListener = getPositiveListener();
                if (loginName.startsWith(ConfigCountry.KEY_AREA_CODE)){
                    dialogData.negativeButtonText = context.getString(R.string.DialogButton_G0);
                    negativeListener = getNegativeListener(context, loginName);
                }
                break;
            case VERIFY_CODE_ERROR:
                dialogData.positiveButtonText = context.getString(R.string.DialogButton_B0);
                positiveListener = getPositiveListener();
                break;
        }
        onShowDialog(context, dialogData, positiveListener, negativeListener);
    }

    @NonNull
    private DialogInterface.OnClickListener getPositiveListener() {
        DialogInterface.OnClickListener positiveListener;
        positiveListener = new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
        return positiveListener;
    }

    @NonNull
    private DialogInterface.OnClickListener getNegativeListener(Context context, String loginName) {
        DialogInterface.OnClickListener negativeListener;
        negativeListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityManager.getInstance().openForgetPwdActivity(context, loginName);
                dialog.dismiss();
            }
        };
        return negativeListener;
    }

    private void onShowDialog(Context context, DialogData dialogData, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        new CommonDialog
                .Builder(context)
                .setMessage(dialogData.errorMsg)
                .setCancelable(false)
                .setPositiveButton(dialogData.positiveButtonText, positiveListener)
                .setNegativeButton(dialogData.negativeButtonText, negativeListener)
                .show();
    }

    class DialogData {
        public String errorMsg;
        public String positiveButtonText;
        public String negativeButtonText;

    }
}

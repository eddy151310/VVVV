package com.ahdi.lib.utils.widgets.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ahdi.lib.utils.R;
import com.ahdi.lib.utils.utils.LogUtil;


/**
 * Created by Lcw on 2016/12/6.
 */

public class LoadingDialog extends ProgressDialog {

    private static final String TAG = "LoadingDialog";
    private static final int SHOW_COMMON_LOADING = 0;
    private static final int SHOW_WHITE_POINT_LOADING = 1;
    private static ProgressDialog progressDialog;
    private String mMessage;

    public void setLoadingType(int loadingType) {
        this.loadingType = loadingType;
    }

    private int loadingType; // 0: 默认菊花loading  1: 三个白点loading动画

    /**
     * loading开关
     */
    public static void showDialog(Context context, String message) {
        Activity activity = ((Activity) context);
        progressDialog = new LoadingDialog(activity, R.style.custom_dialog);
        progressDialog.setMessage(message);
        if (activity != null && !activity.isFinishing()) {
            LogUtil.e(TAG, "show");
            progressDialog.show();
        }
    }

    public static void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            LogUtil.e(TAG, "dismiss");
            progressDialog.dismiss();
        }
    }

    public static LoadingDialog showDialogLoading(Context context, String message) {
        Activity activity = ((Activity) context);
        LoadingDialog progressDialog = new LoadingDialog(activity, R.style.custom_dialog);
        progressDialog.setMessage(message);
        if (activity != null && !activity.isFinishing()) {
            LogUtil.e(TAG, "show");
            progressDialog.setLoadingType(SHOW_COMMON_LOADING);
            progressDialog.show();
        }
        return progressDialog;
    }

    public static void dismissDialog(LoadingDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            LogUtil.e(TAG, "dismiss");
            dialog.dismiss();
        }
    }

    public static LoadingDialog showWhitePointLoading(Context context) {
        Activity activity = ((Activity) context);
        LoadingDialog progressDialog = new LoadingDialog(activity, R.style.custom_dialog);
        if (activity != null && !activity.isFinishing()) {
            LogUtil.e(TAG, "show");
            progressDialog.setLoadingType(SHOW_WHITE_POINT_LOADING);
            progressDialog.show();
        }
        return progressDialog;
    }

    public static void dismissWhitePointLoding(LoadingDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            LogUtil.e(TAG, "dismiss");
            dialog.dismiss();
        }
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public LoadingDialog(Context context) {
        super(context);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public void setMessage(String msg) {
        mMessage = msg;
    }

    public void setMessage(int msgId) {
        mMessage = getContext().getString(msgId);
    }

    @Override
    public void setMessage(CharSequence msg) {
        mMessage = msg.toString();
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Context context = getContext();
        setContentView(R.layout.layout_loading);

        TextView textView = findViewById(R.id.dialog_msg);
        textView.setText(TextUtils.isEmpty(mMessage) ? context.getString(R.string.DialogTitle_C0) : mMessage);

        ProgressBar loading_progress_bar = findViewById(R.id.loading_progress_bar);
        ImageView iv_payCode_loading = findViewById(R.id.iv_payCode_loading);

        if (loadingType == SHOW_COMMON_LOADING) {
            loading_progress_bar.setVisibility(View.VISIBLE);
            iv_payCode_loading.setVisibility(View.GONE);

        } else if (loadingType == SHOW_WHITE_POINT_LOADING) {
            iv_payCode_loading.setVisibility(View.VISIBLE);
            loading_progress_bar.setVisibility(View.GONE);
            AnimationDrawable drawable = (AnimationDrawable) iv_payCode_loading.getDrawable();
            drawable.start();
        }
    }

}

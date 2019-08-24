package com.ahdi.lib.utils.widgets.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.ahdi.lib.utils.R;
import com.ahdi.lib.utils.utils.QRCodeUtil;

/**
 * Date: 2018/8/20 上午10:08
 * Author: kay lau
 * Description:
 */
public class DialogSigupQR extends Dialog {


    public DialogSigupQR(@NonNull Context context) {
        this(context, R.style.custom_dialog);
    }

    public DialogSigupQR(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {

        private Context mContext;
        private boolean mCancelable = false;
        private OnClickListener mNegativeButtonClickListener;
        private String content;

        public Builder(Context context) {
            this.mContext = context;
            this.setCancelable(false);
        }

        public DialogSigupQR.Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public DialogSigupQR.Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public DialogSigupQR.Builder setNegativeButton(OnClickListener listener) {
            this.mNegativeButtonClickListener = listener;
            return this;
        }

        public DialogSigupQR showDialog() {
            DialogSigupQR dialog = onCreateDialog();
            if (dialog != null) {
                dialog.setCancelable(mCancelable);
                dialog.setOwnerActivity((Activity) mContext);
                dialog.setCanceledOnTouchOutside(false);
                Activity mActivity = dialog.getOwnerActivity();
                if (mActivity != null && !mActivity.isFinishing()) {
                    dialog.show();
                    return dialog;
                }
            }
            return null;
        }

        private DialogSigupQR onCreateDialog() {

            final DialogSigupQR dialog = new DialogSigupQR(mContext);
            View layout = LayoutInflater.from(mContext).inflate(R.layout.layout_dialog_sigup_qr, null);
            dialog.setContentView(layout);

            ImageView iv_sigup_qr = layout.findViewById(R.id.iv_sigup_qr);
            ViewTreeObserver viewTreeObserver = iv_sigup_qr.getViewTreeObserver();
            if (viewTreeObserver != null) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        removeViewTreeObserver(viewTreeObserver, this);
                        iv_sigup_qr.setImageBitmap(QRCodeUtil.getQRBitmap(content, iv_sigup_qr));
                    }
                });
            }

            // set the cancel button
            if (mNegativeButtonClickListener != null) {
                layout.findViewById(R.id.iv_dialog_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mNegativeButtonClickListener != null) {
                            mNegativeButtonClickListener.onClick(dialog,
                                    DialogInterface.BUTTON_NEGATIVE);
                        }
                    }
                });
            }
            return dialog;
        }

        private void removeViewTreeObserver(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener listener) {
            if (observer.isAlive()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    observer.removeOnGlobalLayoutListener(listener);
                } else {
                    observer.removeGlobalOnLayoutListener(listener);
                }
            }
        }
    }
}

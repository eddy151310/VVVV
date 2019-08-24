package com.ahdi.lib.utils.widgets.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ahdi.lib.utils.R;


/**
 * @author zhaohe
 * 实名认证引导弹窗
 */

public class IDGuideDialog extends Dialog {

    public static final int DIALOG_MARGIN_96 = 96;

    public IDGuideDialog(Context context) {
        this(context, R.style.custom_dialog);
    }

    public IDGuideDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {

        private Context mContext;
        private boolean mCancelable;
        private boolean messageIsCenter = true;
        private String mTitle;
        private String mMessage;
        private CharSequence mNegativeButtonText;
        private CharSequence mPositiveButtonText;
        private OnClickListener mNegativeButtonClickListener;
        private OnClickListener mPositiveButtonClickListener;

        private TextView mPositiveButton;
        private TextView mNegativeButton;
        private View v_dialog_line_vertical;
        private ScrollView sv_dialog;
        private int color = 0;

        private int margin;

        public Builder(Context context) {
            this.mContext = context;
            this.setCancelable(false);
            this.setMargin(DIALOG_MARGIN_96);
        }

        public Builder setMargin(int margin) {
            this.margin = margin;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public Builder setMessage(String message) {
            this.mMessage = message;
            return this;
        }
        public Builder setTitle(String title){
            this.mTitle = title;
            return this;
        }
        public Builder setMessageCenter(boolean messageIsCenter) {
            this.messageIsCenter = messageIsCenter;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, OnClickListener listener) {
            this.mNegativeButtonText = negativeButtonText;
            this.mNegativeButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.mPositiveButtonText = positiveButtonText;
            this.mPositiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButtonColor(int color){
            this.color = color;
            return this;
        }

        public IDGuideDialog showIDDialog() {
            IDGuideDialog dialog = onCreateIDDialog();
            if(dialog != null){
                dialog.setCancelable(false);
                dialog.setOwnerActivity((Activity) mContext);
                dialog.setCanceledOnTouchOutside(false);
                Activity mactivity = dialog.getOwnerActivity();
                if(mactivity != null && !mactivity.isFinishing()){
                    dialog.show();
                    return dialog;
                }
            }
            return null;
        }

            /**
             * 实名认证引导dialog
             * @return
             */
         private IDGuideDialog onCreateIDDialog() {

                final IDGuideDialog dialog = new IDGuideDialog(mContext);
                View layout = LayoutInflater.from(mContext).inflate(R.layout.layout_dialog_id_guide, null);
                dialog.setContentView(layout);

                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();

                int dialogWidth = getDialogWidth((Activity) mContext, margin);
                layoutParams.width = dialogWidth; // 宽度

                sv_dialog = ((ScrollView) layout.findViewById(R.id.sv_dialog));
                sv_dialog.setVerticalScrollBarEnabled(false);

                // set the confirm button
                if (mPositiveButtonText != null) {
                    mPositiveButton = (TextView) layout.findViewById(R.id.tv_dialog_confirm);
                    mPositiveButton.setText(mPositiveButtonText);
                    mPositiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mPositiveButtonClickListener != null) {
                                mPositiveButtonClickListener.onClick(dialog,
                                        DialogInterface.BUTTON_POSITIVE);
                            }
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
                } else {
                    layout.findViewById(R.id.iv_dialog_close).setVisibility(View.GONE);
                }

                return dialog;
            }


        private int getDialogWidth(Activity activity, int margin) {
            Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            defaultDisplay.getMetrics(outMetrics);
            int widthPixels = outMetrics.widthPixels;
            int heightPixels = outMetrics.heightPixels;
            int dialogWidth;
            if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    || activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                dialogWidth = widthPixels / 2;
            } else {
                if (1920 == heightPixels) {
                    dialogWidth = widthPixels - margin * 2;
                } else {
                    dialogWidth = widthPixels - margin;
                }
            }
            return dialogWidth;
        }
    }


}
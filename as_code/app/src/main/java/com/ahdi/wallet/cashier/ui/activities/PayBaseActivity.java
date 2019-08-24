package com.ahdi.wallet.cashier.ui.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.lib.utils.base.BaseActivity;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.wallet.R;

/**
 * Date: 2018/5/18 上午9:53
 * Author: kay lau
 * Description:
 */
public class PayBaseActivity extends BaseActivity {

    private Dialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initCommonTitle(String titleStr) {
        initTitleTextView(titleStr);
        initTitleBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSoftInput();
                onBackPressed();
            }
        });
    }

    public TextView initTitleTextView(String titleStr) {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(titleStr);
        return tv_title;
    }

    public ImageView initTitleBack() {
        ImageView btn_back = findViewById(R.id.btn_back);
        return btn_back;
    }

    public Button initTitleNext() {
        Button btn_next = findViewById(R.id.btn_next);
        return btn_next;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = null;
    }

    public void showNormalDialog(String msg) {
        new CommonDialog
                .Builder(this)
                .setMessage(msg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.DialogButton_B0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}

package com.ahdi.lib.utils.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ahdi.lib.utils.R;
import com.ahdi.lib.utils.utils.StatusBarUtil;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;

/**
 * Date: 2017/10/10 下午5:20
 * Author: kay lau
 * Description:
 */
public class AppBaseActivity extends BaseActivity {

    private Dialog dialog = null;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        StatusBarUtil.setStatusBar(this);// 收银台这样的半弹层 【不要使用】  ， statusBar 和 主题效果 导致冲突
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void initCommonTitle(String titleStr) {
        initTitleTextView(titleStr);
        initTitleBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSoftInput();
                onBackPressed();
                finish();
            }
        });
    }

    public TextView initTitleTextView(String titleStr) {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(titleStr);
        return tv_title ;
    }


    public Button initTitleBack( ) {
        Button btn_back = findViewById(R.id.btn_back);
        return btn_back ;
    }

    public Button initTitleNext( ) {
        Button btn_next = findViewById(R.id.btn_next);
        return btn_next ;
    }


    protected void openAppSettingDetails(String msg) {
        new CommonDialog
                .Builder(this)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(getString(com.ahdi.lib.utils.R.string.DialogButton_E0), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                }).setNegativeButton(getString(com.ahdi.lib.utils.R.string.DialogButton_A0), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void showErrorDialog(String errorMsg){
        dialog = new CommonDialog
                .Builder(this)
                .setMessage(errorMsg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.DialogButton_B0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
        dialog = null;
    }

}

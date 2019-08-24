package com.ahdi.wallet.app.ui.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ahdi.lib.utils.utils.DeviceUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.wallet.R;

/**
 * Date: 2018/5/25 下午7:52
 * Author: kay lau
 * Description: 设置页面,  点击logout显示的弹层
 */
public class SettingsDialog extends Dialog implements View.OnClickListener {

    private OnSettingsDialogListener listener;
    private Context context;

    public SettingsDialog(@NonNull Context context) {
        super(context);
    }

    public SettingsDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView() {
        //填充对话框的布局
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_dialog_settings, null);
        //初始化控件
        inflate.findViewById(R.id.re_dialog_logout).setOnClickListener(this);
        inflate.findViewById(R.id.re_dialog_cancel).setOnClickListener(this);
        //将布局设置给Dialog
        setContentView(inflate);

        initDialogAttributes();
    }

    private void initDialogAttributes() {
        //获取当前Activity所在的窗体
        Window dialogWindow = getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = DeviceUtil.getScreenWidth((Activity) context);
        //将属性设置给窗体
        dialogWindow.setAttributes(lp);
    }

    public void setOnSettingsDialogListener(OnSettingsDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onClick(View v) {
        if (!ToolUtils.isCanClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.re_dialog_logout) {
            listener.onCallBack();
            dismiss();

        } else if (id == R.id.re_dialog_cancel) {
            dismiss();
        }
    }

    public interface OnSettingsDialogListener {

        void onCallBack();
    }


}

package com.ahdi.lib.utils.widgets.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ahdi.lib.utils.R;
import com.ahdi.lib.utils.utils.DeviceUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.datepicker.WheelView;

import java.util.ArrayList;

/**
 * Date: 2018/5/25 下午7:52
 * Author: kay lau
 * Description:
 */
public class ListSelectDialog extends Dialog implements View.OnClickListener {

    private OnListSelectListener listener;
    private Context context;
    private ArrayList<String> data;
    private WheelView gender_picker;
    private int selectedIndex;
    private String item;

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public ListSelectDialog(@NonNull Context context, ArrayList<String> data) {
        super(context);
        this.context = context;
        this.data = data;
    }

    public ListSelectDialog(@NonNull Context context, int themeResId, ArrayList<String> data, int defaultSelect) {
        super(context, themeResId);
        this.context = context;
        this.data = data;
        this.selectedIndex = defaultSelect;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView() {
        //填充对话框的布局
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_list_picker, null);
        //初始化控件
        inflate.findViewById(R.id.tv_cancel).setOnClickListener(this);
        inflate.findViewById(R.id.tv_select).setOnClickListener(this);
        gender_picker = inflate.findViewById(R.id.gender_picker);
        gender_picker.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                LogUtil.e("TAG", " dialog selectedIndex: " + selectedIndex);
                setSelectedIndex(selectedIndex);
                setItem(item);
            }
        });
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

    public ListSelectDialog setOnGenderDialogListener(OnListSelectListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void show() {
        super.show();
        gender_picker.setItems(data, selectedIndex);
        setSelectedIndex(selectedIndex);
        setItem(data.get(selectedIndex));
    }

    @Override
    public void onClick(View v) {
        if (!ToolUtils.isCanClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.tv_cancel) {
            dismiss();

        } else if (id == R.id.tv_select) {
            if (gender_picker.isStopScroll()){
                listener.onCallBack(selectedIndex, item);
                dismiss();
            }
        }
    }

    /**
     * 默认选中的item
     *
     * @param selectItem
     * @return
     */
    public ListSelectDialog selectItem(String selectItem) {
        if (!TextUtils.isEmpty(selectItem) && data != null && !data.isEmpty()) {
            int selected = data.indexOf(selectItem);
            if (selected != -1) {
                selectedIndex = selected;
            }
        }
        return this;
    }

    public interface OnListSelectListener {

        void onCallBack(int selectedIndex, String item);
    }
}

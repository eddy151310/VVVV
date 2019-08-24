package com.ahdi.wallet.app.ui.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.ui.adapters.TransCateAdapter;
import com.ahdi.lib.utils.listener.OnItemClickListener;
import com.ahdi.wallet.app.ui.bean.TransCategoriesBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/5/25.
 */

public class TransCategoriesView implements View.OnClickListener{
    private Context context = null;
    private Dialog dialog;

    private RecyclerView recyclerView;
    private int lastSelectedType = -1;
    private List<TransCategoriesBean> list = new ArrayList<>();
    private TransCateAdapter adapter = null;
    private TransCategoriesSelect listener = null;

    public TransCategoriesView(Context context, int lastSelected) {
        this.context = context;
        this.lastSelectedType = lastSelected;
        initDialog();
        initView();
        initData();
    }

    private void initDialog() {
        if (dialog == null) {
            dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
            dialog.setCancelable(true);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_select_categories);
            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = dm.widthPixels;
            window.setAttributes(lp);
        }
    }
    private void initView() {
        dialog.findViewById(R.id.rl_cancel).setOnClickListener(this);
        recyclerView = dialog.findViewById(R.id.recyclerView);
    }


    private void initData() {
        list.clear();
        Resources resources = context.getResources();
        list.add(new TransCategoriesBean(Constants.TRANS_TYPE_DEFAULT, resources.getString(R.string.Transactions_D0)));
        list.add(new TransCategoriesBean(Constants.TRANS_TYPE_TRANSFER,resources.getString(R.string.Transactions_E0)));
        //list.add(new TransCategoriesBean(Constants.TRANS_TYPE_ANGPAO,resources.getString(R.string.Transactions_F0)));
        list.add(new TransCategoriesBean(Constants.TRANS_TYPE_RECEIVE_PAY,resources.getString(R.string.Transactions_G0)));
        list.add(new TransCategoriesBean(Constants.TRANS_TYPE_REFUND,resources.getString(R.string.Transactions_H0)));
        list.add(new TransCategoriesBean(Constants.TRANS_TYPE_BALANCE,resources.getString(R.string.Transactions_I0)));
        if (lastSelectedType == -1){
            lastSelectedType = list.get(0).getType();
        }
        adapter = new TransCateAdapter(context, list, lastSelectedType);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!ToolUtils.isCanClick()){
                    return;
                }
                if (listener != null){
                    listener.onSelected(list.get(position).getType(), list.get(position).getTypeString());
                    dismiss();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_cancel){
           dismiss();
        }
    }

    public boolean isShowing(){
        if (dialog != null && dialog.isShowing()){
            return true;
        }
        return false;
    }

    public void show(){
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismiss(){
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public void setListener(TransCategoriesSelect listener) {
        this.listener = listener;
    }

    public interface TransCategoriesSelect {
        /**
         * 选中的种类
         * @param selectedType 选中的类型
         * @param typeString
         */
        void onSelected(int selectedType, String typeString);
    }

}

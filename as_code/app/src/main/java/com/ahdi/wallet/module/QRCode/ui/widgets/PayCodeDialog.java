package com.ahdi.wallet.module.QRCode.ui.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ahdi.wallet.module.QRCode.schemas.AuthCodePaySchema;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.listener.OnItemClickListener;
import com.ahdi.lib.utils.utils.DeviceUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.wallet.R;
import com.ahdi.wallet.module.QRCode.ui.adapter.SelectPayModeAdapter;

import java.util.List;

/**
 * Date: 2018/5/25 下午7:52
 * Author: kay lau
 * Description:
 */
public class PayCodeDialog extends Dialog {

    public static final String TAG = PayCodeDialog.class.getSimpleName();

    private OnPayCodeDialogListener listener;
    private Context context;
    private int mPosition;
    private int screenHeight;
    private String payTypeName;
    private String iconUrl;
    private List<AuthCodePaySchema> paySchemas;

    public PayCodeDialog(@NonNull Context context, int themeResId, int position,
                         List<AuthCodePaySchema> authCodePaySchemas, int screenHeight) {
        super(context, themeResId);
        this.context = context;
        this.mPosition = position;
        this.screenHeight = screenHeight;
        paySchemas = authCodePaySchemas;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        //填充对话框的布局
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_dialog_paycode, null);
        //初始化控件
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        tv_title.setText(context.getString(R.string.ShowPayQR_D0));
        View backView = inflate.findViewById(R.id.btn_back);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ToolUtils.isCanClick()) {
                    return;
                }
                dismiss();
            }
        });

        initListView(inflate);
        //将布局设置给Dialog
        setContentView(inflate);

        initDialogAttributes();
    }

    private void initDialogAttributes() {
        //获取当前Activity所在的窗体
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = DeviceUtil.getScreenWidth((Activity) context);
            lp.height = ((int) (screenHeight * Constants.LOCAL_DIALOG_HEIGHT_SCALE));
            //将属性设置给窗体
            dialogWindow.setAttributes(lp);
            //设置Dialog从窗体底部弹出
            dialogWindow.setGravity(Gravity.BOTTOM);
        }
    }

    private void initListView(View inflate) {
        RecyclerView listView = inflate.findViewById(R.id.payCode_payType_list);
        SelectPayModeAdapter adapter = new SelectPayModeAdapter(context, paySchemas, mPosition);
        listView.setLayoutManager(new LinearLayoutManager(context));
        listView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtil.e(TAG, "position: " + position);
                if (paySchemas != null && paySchemas.size() >= position) {
                    payTypeName = paySchemas.get(position).Bank;
                    iconUrl = paySchemas.get(position).BankIcon;
                }
                mPosition = position;
                dismiss();
            }
        });
        AuthCodePaySchema schema = paySchemas.get(mPosition);
        if (schema != null) {
            payTypeName = schema.Bank;
            iconUrl = schema.BankIcon;
        }
    }

    public void setOnPayCodeDialogListener(OnPayCodeDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        listener.onCallBack(mPosition, payTypeName, iconUrl);
    }

    public interface OnPayCodeDialogListener {

        void onCallBack(int position, String payTypeName, String iconUrl);
    }
}

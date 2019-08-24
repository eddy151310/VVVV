package com.ahdi.wallet.app.ui.widgets;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.wallet.R;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/5/25.
 */

public class SelectPhotoView implements View.OnClickListener{
    /**打开相机*/
    public static final int TYPE_CAMERA = 0;
    /**打开相册*/
    public static final int TYPE_ALBUM = 1;
    /**取消*/
    public static final int TYPE_CANCEL = 2;

    private Context context = null;
    private Dialog dialog;

    private TextView tv_take_photo, tv_album, tv_cancel;

    private SelectPhotoListener listener = null;

    public SelectPhotoView(Context context) {
        this.context = context;
        initDialog();
        initView();
    }

    private void initDialog() {
        if (dialog == null) {
            dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
            dialog.setCancelable(true);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_dialog_select_photo);
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
        tv_take_photo = dialog.findViewById(R.id.tv_take_photo);
        tv_album = dialog.findViewById(R.id.tv_album);
        tv_cancel = dialog.findViewById(R.id.tv_cancel);
        tv_take_photo.setOnClickListener(this);
        tv_album.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!ToolUtils.isCanClick()){
            return;
        }
        if (listener == null){
            return;
        }
        if (v.getId() == R.id.tv_take_photo){
            listener.onSelectType(TYPE_CAMERA);
        }else if (v.getId() == R.id.tv_album){
            listener.onSelectType(TYPE_ALBUM);
        }else if (v.getId() == R.id.tv_cancel){
            listener.onSelectType(TYPE_CANCEL);
        }
        dismiss();
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

    public void setListener(SelectPhotoListener listener) {
        this.listener = listener;
    }


    public interface SelectPhotoListener {
        /**
         * 选择
         * @param type
         */
       void onSelectType(int type);
    }

}

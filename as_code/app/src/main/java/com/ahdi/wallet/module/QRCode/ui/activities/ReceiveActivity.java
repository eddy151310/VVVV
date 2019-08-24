package com.ahdi.wallet.module.QRCode.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.module.QRCode.response.CollectQRCodeRsp;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AmountUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.QRCodeUtil;
import com.ahdi.lib.utils.utils.StatusBarUtil;
import com.ahdi.lib.utils.widgets.ImgUtils;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.R;
import com.ahdi.wallet.module.QRCode.QRCodeSdk;
import com.ahdi.wallet.module.QRCode.callback.QRCodeSdkCallBack;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class ReceiveActivity extends AppBaseActivity {

    private static final String TAG = ReceiveActivity.class.getSimpleName();
    /**
     * SD卡的写入权限的请求码
     */
    private static final int REQUEST_WRITE_STORAGE_PERMISSION = 101;
    private static final int REQUEST_OPEN_SET_AMOUNT = 102;
    public static final int RESULT_CLOSE_SET_AMOUNT = 103;
    public static final int MSG_SUCCESS = 0;

    private ImageView iv_receive_code, iv_qr_refresh;
    private TextView tv_receive_amount, tv_receive_left, tv_saveImage;
    private MyHandler handler;
    private String uri;
    private String amount;

    public class MyHandler extends Handler {

        private WeakReference<ReceiveActivity> reference;
        private LoadingDialog loadingDialog;

        public MyHandler(ReceiveActivity activity, LoadingDialog loadingDialog) {
            reference = new WeakReference<>(activity);
            this.loadingDialog = loadingDialog;
        }

        @Override
        public void handleMessage(Message msg) {
            ReceiveActivity activity = reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case MSG_SUCCESS:
                    if (iv_receive_code != null) {
                        iv_receive_code.setImageBitmap((Bitmap) msg.obj);
                    }
                    LoadingDialog.dismissDialog(loadingDialog);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            LogUtil.e(TAG, "isMemoryRecover   ");
            return;
        }
        initQRCode();
        LogUtil.e(TAG, "initQRCode  ");
        setContentView(R.layout.activity_collect_qr_code);
        StatusBarUtil.setStatusBar(this, R.color.CC_D63031, false);
        initTitleView();
        initView();
        initData();
    }

    private void initTitleView() {
        View view = findViewById(R.id.title);
        view.setBackgroundResource(R.color.CC_00000000);
        //初始化控件
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.ReceiveQR_A0));
        tv_title.setTextColor(getResources().getColor(R.color.CC_FFFFFF));
        Button btn_backView = view.findViewById(R.id.btn_back);
        btn_backView.setBackgroundResource(R.drawable.selector_btn_title_back_white);
        btn_backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCanClick()) {
                    return;
                }
                onClose();
            }
        });
    }

    private void initView() {
        iv_receive_code = findViewById(R.id.iv_receive_code);
        tv_receive_amount = findViewById(R.id.tv_receive_amount);

        iv_qr_refresh = findViewById(R.id.iv_qr_refresh);
        iv_qr_refresh.setVisibility(View.GONE);

        // 底部左边按钮, 根据是否设置金额, 显示不同文案, 默认是设置金额
        tv_receive_left = findViewById(R.id.tv_receive_left);
        tv_saveImage = findViewById(R.id.tv_saveImage);
    }

    private void initData() {
        //默认不显示金额
        clearAmount();
        tv_receive_left.setOnClickListener(this);
        tv_saveImage.setOnClickListener(this);
        iv_qr_refresh.setOnClickListener(this);
    }

    private void refreshQRCode(String amount) {
        this.amount = amount;
        onCollect(amount);
    }

    private void clearAmount() {
        amount = "";
        tv_receive_amount.setVisibility(View.GONE);
        tv_receive_left.setText(getString(R.string.ReceiveQR_C0)); // 设置金额
    }

    private void setAmount(String finalAmount) {
        tv_receive_amount.setVisibility(View.VISIBLE);
        tv_receive_amount.setText(ConfigCountry.KEY_CURRENCY_SYMBOL.concat(finalAmount));
        tv_receive_left.setText(getString(R.string.ReceiveQR_D0)); // 清除金额
    }

    private void initQRCode() {
        onCollect("");
    }

    /**
     * 获取收款码
     *
     * @param amount
     */
    private void onCollect(String amount) {
        amount = AmountUtil.unFormatString(amount);
        LogUtil.e(TAG, "onCollect 获取收款码, 金额：" + amount);
        LoadingDialog loadingDialog = showLoading();
        String finalAmount = amount;
        QRCodeSdk.collectQR(this, amount, GlobalApplication.getApplication().getSID(), new QRCodeSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                /**显示金额布局*/
                if (!TextUtils.isEmpty(finalAmount)) {
                    setAmount(AmountUtil.formatAmount(finalAmount));
                }
                /**先把已有的二维码清理掉*/
                iv_receive_code.setImageBitmap(null);
                /**处理接口返回数据*/
                onCreateReceiveCode(code, errorMsg, jsonObject, finalAmount);
            }
        });
    }

    private void onCreateReceiveCode(String code, String errorMsg, JSONObject jsonObject, String finalAmount) {

        CollectQRCodeRsp resp = CollectQRCodeRsp.decodeJson(CollectQRCodeRsp.class, jsonObject);
        if (resp != null) {
            if (TextUtils.equals(code, QRCodeSdk.LOCAL_PAY_SUCCESS)) {
                // 后台返回 code = 0 处理
                handleSuccessState(finalAmount, resp);
            } else {
                // 后台返回 code != 0 处理
                handleFailState(errorMsg, finalAmount);
            }
        } else {
            showToast(getString(R.string.Toast_A0));
            showQRFailView(finalAmount);
        }
    }

    private void handleFailState(String errorMsg, String finalAmount) {
        showToast(errorMsg);
        showQRFailView(finalAmount);
    }

    private void handleSuccessState(String finalAmount, CollectQRCodeRsp resp) {
        runOnUiThread(() -> {
            //生成无金额的二维码
            if (TextUtils.isEmpty(finalAmount)) {
                uri = resp.uri;
            }
            createQR(finalAmount, resp.uri);
        });
    }

    /**
     * 生成二维码
     *
     * @param amount 金额可以为空
     * @param uri    二维码内容
     */
    private void createQR(String amount, String uri) {
        if (TextUtils.isEmpty(uri)) {
            showQRFailView(amount);
            return;
        }
        Bitmap qrBitmap = QRCodeUtil.getQRBitmap(uri, iv_receive_code);
        if (qrBitmap != null) {
            handler = new MyHandler(ReceiveActivity.this, showLoading());
            handler.obtainMessage(MSG_SUCCESS, qrBitmap).sendToTarget();
            showQRSuccessView();
        } else {
            showToast(getString(R.string.Toast_A0));
            showQRFailView(amount);
        }
    }

    /**
     * 显示二维码成功，刷新布局
     */
    private void showQRSuccessView() {
        iv_qr_refresh.setVisibility(View.GONE);
        tv_receive_left.setEnabled(true);
        tv_saveImage.setEnabled(true);
    }

    /**
     * 显示二维码失败，刷新布局
     */
    private void showQRFailView(String amount) {
        iv_qr_refresh.setVisibility(View.VISIBLE);
        tv_receive_left.setEnabled(!TextUtils.isEmpty(amount));
        tv_saveImage.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        if (!isCanClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_receive_left:
                if (TextUtils.equals(tv_receive_left.getText(), getString(R.string.ReceiveQR_D0))) {
                    clearAmount();
                    createQR("", uri);
                } else {
                    openSetAmount();
                }
                break;
            case R.id.tv_saveImage:
                checkStoragePer();
                break;
            case R.id.iv_qr_refresh:
                onCollect(amount);
                break;
            default:
                break;
        }
    }

    private void openSetAmount() {
        Intent intent = new Intent(this, ReceiveSetAmountActivity.class);
        startActivityForResult(intent, REQUEST_OPEN_SET_AMOUNT);
        onBottom_in_Activity();
    }

    /**
     * 检查sd卡的读权限，做相应的处理
     */
    private void checkStoragePer() {

        if (Build.VERSION.SDK_INT < 23 || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            saveImage();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            openAppSettingDetails(getString(R.string.DialogMsg_A0));
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE_PERMISSION);
        }
    }

    public void saveImage() {
        boolean isSaveSuccess = ImgUtils.saveImageToGallery(ReceiveActivity.this, ImgUtils.getViewBitmap(iv_receive_code));
        if (isSaveSuccess) {
            showToast(getString(R.string.Toast_C0));
        } else {
            showToast(getString(R.string.Toast_D0));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE_PERMISSION && grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveImage();
        } else if (requestCode == REQUEST_WRITE_STORAGE_PERMISSION) {
            openAppSettingDetails(getString(R.string.DialogMsg_A0));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyHandler();
    }

    private void destroyHandler() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_SET_AMOUNT
                && resultCode == RESULT_CLOSE_SET_AMOUNT
                && data != null) {
            refreshQRCode(data.getStringExtra(Constants.LOCAL_AMOUNT_KEY));
        }
    }

    private void onClose() {
        setResult(PayQRCodeActivity.RESULT_CLOSE_RECEIVE);
        finish();
    }

    @Override
    public void onBackPressed() {
        onClose();
    }
}

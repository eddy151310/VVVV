package com.ahdi.wallet.module.QRCode.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahdi.wallet.module.QRCode.response.PayCodePINConfirmRsp;
import com.ahdi.wallet.module.QRCode.bean.PayCodePINConfirmBean;
import com.ahdi.wallet.module.QRCode.QRCodeSdk;
import com.ahdi.wallet.module.QRCode.callback.QRCodeSdkCallBack;
import com.ahdi.wallet.app.schemas.PayOrderSchema;
import com.ahdi.wallet.app.schemas.TouchSchema;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.BaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.cryptor.aes.AesKeyCryptor;
import com.ahdi.lib.utils.fingerprint.FingerprintSDK;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.DeviceUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.lib.utils.widgets.keyboard.KeyboardUtil;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;

import org.json.JSONObject;

/**
 * Date: 2018/12/3 下午5:58
 * Author: kay lau
 * Description: 支付密码确认界面
 */
public class PayCodeVerifyPINActivity extends BaseActivity {

    public static final String TAG = PayCodeVerifyPINActivity.class.getSimpleName();

    public static final String CONFIRM_PAY_PWD_SUCCESS = "success";
    private static final String CONFIRM_PAY_PWD_CANCEL = "cancel";
    private static final String CONFIRM_PAY_PWD_TIME_OUT = "time_out";
    public static final String CONFIRM_PAY_PWD_VERIFY_OTP = "verify bac otp";

    private ImageView[] imageViews;
    private KeyboardUtil keyboardUtil;
    private String payEx;
    private String OT;
    private String TT;
    private String errMsg;
    private String iv;
    private int cancel = -10;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 设置自定义主题从底部弹出, 否则, 在清单文件设置dialog主题之后, 界面打开之后再屏幕居中显示.
        // statusBar 和 主题效果 导致冲突, 修改为继承base
        setTheme(R.style.PayHubTheme);
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData(getIntent());
        setContentView(R.layout.dialog_activity_paycode_input_pwd);
        initTitleView();
        initView();
    }

    private void initIntentData(Intent intent) {
        if (intent == null) {
            return;
        }
        payEx = intent.getStringExtra(Constants.LOCAL_PAY_EX_KEY);
        TT = intent.getStringExtra(Constants.LOCAL_TT_KEY);
        OT = intent.getStringExtra(Constants.LOCAL_OT_KEY);
        errMsg = intent.getStringExtra(Constants.MSG_KEY);
        iv = intent.getStringExtra("iv");
    }

    private void initTitleView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.PayQRInputPWD_A0));
        View backView = findViewById(R.id.btn_back);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCanClick()) {
                    return;
                }
                showDialogClosed(getString(R.string.DialogMsg_H0));
            }
        });
    }

    private void initView() {
        // 自定义设置屏幕高度
        initRootViewHeight();

        // 初始化错误提示文案
        RelativeLayout rl_error_tips = findViewById(R.id.rl_error_tips);
        if (!TextUtils.isEmpty(errMsg)) {
            TextView tv_error_tips = findViewById(R.id.tv_error_tips);
            tv_error_tips.setText(errMsg);
            rl_error_tips.setVisibility(View.VISIBLE);

        } else {
            rl_error_tips.setVisibility(View.INVISIBLE);
        }

        View pwd_layout = findViewById(R.id.pwd_layout);
        EditText editText = pwd_layout.findViewById(R.id.et);
        imageViews = new ImageView[6];
        imageViews[0] = pwd_layout.findViewById(R.id.item_code_iv1);
        imageViews[1] = pwd_layout.findViewById(R.id.item_code_iv2);
        imageViews[2] = pwd_layout.findViewById(R.id.item_code_iv3);
        imageViews[3] = pwd_layout.findViewById(R.id.item_code_iv4);
        imageViews[4] = pwd_layout.findViewById(R.id.item_code_iv5);
        imageViews[5] = pwd_layout.findViewById(R.id.item_code_iv6);

        keyboardUtil = new KeyboardUtil(this, editText, 2, new InputPwdKeyboardCallback());
        keyboardUtil.showKeyboard();
    }

    private void initRootViewHeight() {
        int height = DeviceUtil.getScreenHeight(this);
        RelativeLayout bg_view = findViewById(R.id.bg_view);
        ViewGroup.LayoutParams layoutParams = bg_view.getLayoutParams();
        layoutParams.height = ((int) (height * Constants.LOCAL_DIALOG_HEIGHT_SCALE));
        bg_view.setLayoutParams(layoutParams);
        bg_view.setBackgroundResource(R.drawable.bg_dialog_payhub_title);
    }

    private class InputPwdKeyboardCallback implements KeyboardUtil.CallBack {

        @Override
        public void onCallback(String result) {
            // 输入支付密码
            onAuthCodeConfirm(result);
        }

        @Override
        public void onPwdInputCallback(StringBuffer buffer, String pwdStr) {
            for (int i = 0; i < buffer.length(); i++) {
                ImageView imageView = imageViews[i];
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.password_black_point);

            }
        }

        @Override
        public void onPwdStrCallback(StringBuffer stringBuffer) {
            imageViews[stringBuffer.length()].setVisibility(View.INVISIBLE);
        }
    }

    private void onClearKeyboard() {
        if (keyboardUtil != null) {
            keyboardUtil.onDeleteAll();
        }
    }

    /**
     * 支付授权码确认
     *
     * @param result
     */
    private void onAuthCodeConfirm(String result) {
        loadingDialog = showLoading();
        PayCodePINConfirmBean bean = new PayCodePINConfirmBean();
        bean.setOT(OT);
        bean.setTT(TT);
        bean.setPayEx(payEx);
        bean.setCancel(cancel);
        bean.setPayAuth(AesKeyCryptor.encodePayPwd(result));
        onAuthCode(bean);
    }

    /**
     * 支付授权码取消
     */
    private void onAuthCodeCancel() {
        PayCodePINConfirmBean bean = new PayCodePINConfirmBean();
        bean.setOT(OT);
        bean.setTT(TT);
        bean.setPayEx(payEx);
        bean.setCancel(cancel);
        onAuthCode(bean);
    }

    /**
     * 支付授权码确认/取消
     *
     * @param bean
     */
    private void onAuthCode(PayCodePINConfirmBean bean) {
        QRCodeSdk.payCodePINConfirm(this, bean, Constants.PAY_AUTH_TYPE_PWD, GlobalApplication.getApplication().getSID(),
                new QRCodeSdkCallBack() {
                    @Override
                    public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                        if (loadingDialog != null) {
                            closeLoading(loadingDialog);
                            onClearKeyboard();
                        }
                        if (bean.getCancel() == 1) {
                            // 取消上报响应时直接return, 防止网络超时 空指针异常
                            return;
                        }
                        PayCodePINConfirmRsp response = PayCodePINConfirmRsp.decodeJson(PayCodePINConfirmRsp.class, jsonObject);
                        if (response != null && TextUtils.equals(code, QRCodeSdk.LOCAL_PAY_SUCCESS)) {
                            if (!TextUtils.isEmpty(response.PayParam)) {
                                // 不需要采集支付密码, 但是需要打开bca otp
                                onClosed(CONFIRM_PAY_PWD_VERIFY_OTP, response.OT, response.OT, response.getPayOrder());
                            } else {
                                // 返回pay码界面开始查询支付结果逻辑
                                if (!TextUtils.isEmpty(iv)) {
                                    FingerprintSDK.putIv(PayCodeVerifyPINActivity.this, iv);
                                }
                                TouchSchema touchSchema = response.getTouchSchema();
                                if (touchSchema != null) {
                                    String lName = AppGlobalUtil.getInstance().getLName(PayCodeVerifyPINActivity.this);
                                    TouchIDStateUtil.saveTouchIDPayKey(PayCodeVerifyPINActivity.this, lName, touchSchema.Key);
                                    TouchIDStateUtil.saveTouchIDPayVer(PayCodeVerifyPINActivity.this, lName, touchSchema.Ver);
                                }
                                onClosed(CONFIRM_PAY_PWD_SUCCESS, response.OT, response.TT);
                            }
                        } else if (TextUtils.equals(Constants.RET_CODE_A203, code)/*支付密码错误*/) {
                            showErrorDialog(errorMsg);

                        } else if (TextUtils.equals(Constants.RET_CODE_A206, code)/*密码被锁定*/
                                || TextUtils.equals(Constants.RET_CODE_A010, code)/*账户被锁定*/) {
                            showDialogGoBackToHome(errorMsg);

                        } else if (TextUtils.equals(Constants.RET_CODE_6013, code)/*pay码支付超时*/) {
                            showDialogPayTimeOut(errorMsg);

                        } else {
                            LogUtil.e(TAG, errorMsg);
                            showToast(errorMsg);
                        }
                    }
                });
    }

    private void showDialogGoBackToHome(String errorMsg) {
        new CommonDialog
                .Builder(this)
                .setMessage(errorMsg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.DialogButton_B0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityManager.getInstance().openMainActivity(PayCodeVerifyPINActivity.this);
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
    }

    private void onClosed(String verifyPWDResult, String OT, String TT) {
        onClosed(verifyPWDResult, OT, TT, null);
    }

    private void onClosed(String verifyPWDResult, String OT, String TT, PayOrderSchema payOrderSchema) {
        Intent intent = new Intent();
        intent.putExtra(Constants.LOCAL_RESULT_KEY, verifyPWDResult);
        if (!TextUtils.isEmpty(TT)) {
            intent.putExtra(Constants.LOCAL_TT_KEY, TT);
        }
        if (!TextUtils.isEmpty(OT)) {
            intent.putExtra(Constants.LOCAL_OT_KEY, OT);
        }
        if (payOrderSchema != null) {
            intent.putExtra(Constants.LOCAL_PAY_ORDER_KEY, payOrderSchema);
        }
        setResult(PayQRCodeActivity.RESULT_CLOSE_INPUT_PWD, intent);
        finish();
        onBottom_out_Activity();
    }

    private void showDialogClosed(String msg) {
        new CommonDialog
                .Builder(this)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.DialogButton_B0), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // 关闭输入密码弹层, 取消支付上报, 后台只上报一次, 忽略上传结果
                        cancel = 1; // 值固定为1。代表用户取消支付
                        onAuthCodeCancel();
                        dialog.dismiss();
                        onClosed(CONFIRM_PAY_PWD_CANCEL, "", "");
                    }
                }).setNegativeButton(getString(R.string.DialogButton_A0), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void showErrorDialog(String errorMsg) {
        new CommonDialog
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

    private void showDialogPayTimeOut(String msg) {
        new CommonDialog
                .Builder(this)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.DialogButton_B0), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onClosed(CONFIRM_PAY_PWD_TIME_OUT, "", "");
                    }
                }).show();
    }
}

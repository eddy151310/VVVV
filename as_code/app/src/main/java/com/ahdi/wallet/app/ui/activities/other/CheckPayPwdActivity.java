package com.ahdi.wallet.app.ui.activities.other;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.sdk.AccountSdk;
import com.ahdi.wallet.app.callback.AccountSdkCallBack;
import com.ahdi.wallet.app.main.AccountSdkMain;
import com.ahdi.wallet.app.response.CheckPayPwdRsp;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.StringUtil;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.cryptor.aes.AesKeyCryptor;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.keyboard.KeyboardUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckPayPwdActivity extends AppBaseActivity {

    private static final String TAG = CheckPayPwdActivity.class.getSimpleName();

    private EditText editText;
    private ImageView[] imageViews;
    private String sid;
    private int ttype = -1;

    private KeyboardUtil keyboardUtil;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData(getIntent());
        setContentView(R.layout.activity_check_pay_pwd);
        initTitle();
        initView();
    }

    private void initTitle() {
        tv_title = initTitleTextView("");
        initTitleBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCanClick()) {
                    return;
                }
                AccountSdkMain.getInstance().checkPayPwdCallback(AccountSdk.LOCAL_PAY_USER_CANCEL, "", null);
                AccountSdkMain.getInstance().onDestroy();
                finish();
            }
        });

    }

    private void initIntentData(Intent intent) {
        if (intent != null) {
            sid = intent.getStringExtra(Constants.LOCAL_SID_KEY);
            ttype = intent.getIntExtra(Constants.LOCAL_TTYPE_KEY, -1);
        }
    }

    private void initView() {
        View view = findViewById(R.id.check_pay_pwd_layout);
        TextView tv_tips = view.findViewById(R.id.tv_tip_one);
        TextView tv_forgot_pin = view.findViewById(R.id.tv_forgot_pin);
        tv_forgot_pin.setVisibility(View.GONE);

        if (ttype == AccountSdk.PWD_CHECK_TTYPE_MODIFY) { //修改支付密码 0
            tv_title.setText(getString(R.string.ModifyPayPWDCheckPayPWD_A0));
            tv_tips.setText(getString(R.string.ModifyPayPWDCheckPayPWD_B0));
            // 非62的区号, 隐藏OTP页面入口
            refreshForgetPIN(tv_forgot_pin);
        } else if (ttype == AccountSdk.PWD_CHECK_TTYPE_BINDBANKACCOUNT) {//增加提现账户 5
            tv_title.setText(getString(R.string.BankAccountAddCheckPWD_A0));
            tv_tips.setText(getString(R.string.BankAccountAddCheckPWD_B0));
        } else if (ttype == AccountSdk.PWD_CHECK_TTYPE_NOT) {// 开通指纹支付
            tv_title.setText(getString(R.string.BiometryPayVerifyPIN_A0));
            tv_tips.setText(getString(R.string.BiometryPayVerifyPIN_B0));
        }

        View pwd_layout = view.findViewById(R.id.pwd_layout);
        editText = pwd_layout.findViewById(R.id.et);
        imageViews = new ImageView[6];
        imageViews[0] = pwd_layout.findViewById(R.id.item_code_iv1);
        imageViews[1] = pwd_layout.findViewById(R.id.item_code_iv2);
        imageViews[2] = pwd_layout.findViewById(R.id.item_code_iv3);
        imageViews[3] = pwd_layout.findViewById(R.id.item_code_iv4);
        imageViews[4] = pwd_layout.findViewById(R.id.item_code_iv5);
        imageViews[5] = pwd_layout.findViewById(R.id.item_code_iv6);
        intKeyBoardView();
    }

    private void refreshForgetPIN(TextView tv_forgot_pin) {
        String areaCode = AppGlobalUtil.getInstance().getString(this, Constants.LOCAL_AREA_CODE_KEY);
        if (TextUtils.equals(areaCode, ConfigCountry.KEY_AREA_CODE)) {
            tv_forgot_pin.setVisibility(View.VISIBLE);
            tv_forgot_pin.setOnClickListener(this);
        }
    }

    public void intKeyBoardView() {
        keyboardUtil = new KeyboardUtil(this, editText, 2, new KeyboardCallback());
        keyboardUtil.showKeyboard();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_forgot_pin) {
            openForgotPIN();
        }
    }

    /**
     * 打开忘记支付密码: 发送短信界面
     */
    private void openForgotPIN() {
        try {
            Class<?> clazz = Class.forName(ActivityManager.forgetPayPwdActivityPackageName);
            startActivity(new Intent(this, clazz));
            finish();
        } catch (ClassNotFoundException e) {
            LogUtil.e(TAG, e.toString());
        }
    }

    private class KeyboardCallback implements KeyboardUtil.CallBack {

        @Override
        public void onCallback(String result) {
            checkPayPwd(result);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void checkPayPwd(String pwd) {
        LoadingDialog loadingDialog = showLoading();
        String random = StringUtil.getStringRandom(16);
        String payPwd = AesKeyCryptor.encodePayPwd(pwd, random);
        AccountSdkMain.getInstance().checkPayPwd(this, sid, payPwd, ttype, new AccountSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                try {
                    closeLoading(loadingDialog);
                    if (TextUtils.equals(code, AccountSdk.LOCAL_PAY_SUCCESS)) {
                        CheckPayPwdRsp resp = CheckPayPwdRsp.decodeJson(CheckPayPwdRsp.class, jsonObject);
                        if (ttype == AccountSdk.PWD_CHECK_TTYPE_NOT) {
                            JSONObject json = new JSONObject();
                            try {
                                json.put(Constants.LOCAL_pwd_KEY, payPwd);
                                json.put(Constants.LOCAL_random_KEY, random);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            AccountSdkMain.getInstance().checkPayPwdCallback(AccountSdk.LOCAL_PAY_SUCCESS, errorMsg, json);

                        } else if (!TextUtils.isEmpty(resp.getToken())) {
                            AccountSdkMain.getInstance().checkPayPwdCallback(AccountSdk.LOCAL_PAY_SUCCESS, resp.getToken(), jsonObject);
                        }
                        finish();
                    } else {
                        checkErrorResult(code, errorMsg);
                    }
                    onClearKeyboard();
                } catch (Exception e) {
                    LogUtil.e(TAG, e.toString());
                }
            }
        });
    }

    public void checkErrorResult(String code, String errorMsg) {
        if (code.equals(Constants.RET_CODE_A203)) {
            showDialog(errorMsg);
        } else if (code.equals(Constants.RET_CODE_A206)) {
            showControlDialog(errorMsg);
        } else {
            ToastUtil.showToastLong(this, errorMsg);
        }
    }

    public void showDialog(String msg) {
        new CommonDialog
                .Builder(this)
                .setMessage(msg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.DialogButton_D0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onClearKeyboard();
                    }
                }).show();
    }

    public void showControlDialog(String msg) {
        new CommonDialog
                .Builder(this)
                .setMessage(msg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.General_C0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onClearKeyboard();
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AccountSdkMain.getInstance().onDestroy();
    }
}

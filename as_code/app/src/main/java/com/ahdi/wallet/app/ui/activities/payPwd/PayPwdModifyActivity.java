package com.ahdi.wallet.app.ui.activities.payPwd;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.app.sdk.AccountSdk;
import com.ahdi.wallet.app.callback.AccountSdkCallBack;
import com.ahdi.lib.utils.cryptor.aes.AesKeyCryptor;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.bean.UserData;
import com.ahdi.lib.utils.utils.StringUtil;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.lib.utils.widgets.keyboard.KeyboardUtil;

import org.json.JSONObject;

public class PayPwdModifyActivity extends AppBaseActivity {

    private EditText edt_pwd;
    private Button btn_save;
    private TextView tv_enter_pwd_for, tv_enter_pwd, tv_user_info;
    private ImageView[] imageViews;
    private KeyboardUtil keyboardUtil;
    private String token;
    private String payPwd;
    private String firstPwd;
    private int from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData(getIntent());
        setContentView(R.layout.activity_pay_pwd_modify);
        initTitle();
        initView();
    }

    private void initIntentData(Intent intent) {
        if (intent != null) {
            firstPwd = intent.getStringExtra(Constants.LOCAL_pwd_KEY);
            token = intent.getStringExtra(Constants.LOCAL_TOKEN_KEY);
            from = intent.getIntExtra(Constants.LOCAL_FROM_KEY, 0);
        }
    }

    private void initTitle() {
        initTitleBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });
        if (from == Constants.LOCAL_PAYMENT_FROM_GUIDE_SET) {
            initTitleTextView(getString(R.string.GuideSetPayPWDPage2_A0));

        } else if (from == Constants.LOCAL_PAYMENT_FROM_RESET) {
            initTitleTextView(getString(R.string.ModifyPayPWDPage2_A0));
        }
    }


    public void initView() {
        tv_enter_pwd_for = findViewById(R.id.tv_enter_pwd_for);
        tv_enter_pwd = findViewById(R.id.tv_enter_pwd);
        tv_user_info = findViewById(R.id.tv_user_info);
        btn_save = findViewById(R.id.btn_save);

        View pwd_layout = findViewById(R.id.pwd_layout);
        edt_pwd = pwd_layout.findViewById(R.id.et);

        imageViews = new ImageView[6];
        imageViews[0] = pwd_layout.findViewById(R.id.item_code_iv1);
        imageViews[1] = pwd_layout.findViewById(R.id.item_code_iv2);
        imageViews[2] = pwd_layout.findViewById(R.id.item_code_iv3);
        imageViews[3] = pwd_layout.findViewById(R.id.item_code_iv4);
        imageViews[4] = pwd_layout.findViewById(R.id.item_code_iv5);
        imageViews[5] = pwd_layout.findViewById(R.id.item_code_iv6);

        intKeyBoardView();

        initData();
    }

    public void intKeyBoardView() {
        keyboardUtil = new KeyboardUtil(this, edt_pwd, 2, new PayPwdModifyActivity.KeyboardCallback());
        keyboardUtil.showKeyboard();
    }

    @Override
    public void onClick(View view) {
        if (!isCanClick()) {
            return;
        }
        if (view.getId() == R.id.btn_save) {
            if (TextUtils.equals(firstPwd, payPwd)) {
                if (from == Constants.LOCAL_PAYMENT_FROM_GUIDE_SET) {
                    setPwd();
                } else if (from == Constants.LOCAL_PAYMENT_FROM_RESET) {
                    resetPwd();
                }
            }
        }
    }

    private class KeyboardCallback implements KeyboardUtil.CallBack {

        @Override
        public void onCallback(String result) {
            payPwd = result;
            if (!TextUtils.equals(result, firstPwd)) {
                showTimeToast(getString(R.string.Toast_M0), 3 * 1000);
                finish();
            } else {
                btn_save.setEnabled(true);
            }
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
            if (stringBuffer.length() <= 5) {
                btn_save.setEnabled(false);
            } else {
                btn_save.setEnabled(true);
            }
        }
    }

    private void onClearKeyboard() {
        if (keyboardUtil != null) {
            keyboardUtil.onDeleteAll();
        }
    }

    /**
     * 重置密码
     */
    public void resetPwd() {
        LoadingDialog loadingDialog = showLoading();
        AppMain.getInstance().resetPayPwd(this, payPwd, token, 0, new AccountSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                if (TextUtils.equals(code, AccountSdk.LOCAL_PAY_SUCCESS)) {
                    showToast(getString(R.string.Toast_L0));
                    GlobalApplication.getApplication().updateAccountStatus(Constants.STATUE_SAFE_KEY);
                    setResult(PayPwdGuideSetActivity.RESULT_RESET_PAY_PWD);
                    finish();
                } else if (TextUtils.equals(code, Constants.RET_CODE_A204)
                        || TextUtils.equals(code, Constants.RET_CODE_A205)) {
                    showToast(errorMsg);
                    finish();
                } else if (TextUtils.equals(code, Constants.RET_CODE_A207)) {
                    showTimeToast(errorMsg, 3 * 1000);
                    onClearKeyboard();
                    finish();
                } else {
                    showToast(errorMsg);
                    onClearKeyboard();
                }
            }
        });
    }

    /**
     * 设置密码
     */
    public void setPwd() {
        LoadingDialog loadingDialog = showLoading();
        String random = StringUtil.getStringRandom(16);
        String payPwdCipher = AesKeyCryptor.encodePayPwd(payPwd, random);
        AppMain.getInstance().setPayPwd(this, payPwdCipher, 0, new AccountSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                if (TextUtils.equals(code, AccountSdk.LOCAL_PAY_SUCCESS)) {
                    showToast(getString(R.string.Toast_L0));
                    GlobalApplication.getApplication().updateAccountStatus(Constants.STATUE_SAFE_KEY);
                    Intent intent = new Intent();
                    intent.putExtra(Constants.LOCAL_pwd_KEY, payPwdCipher);
                    intent.putExtra(Constants.LOCAL_random_KEY, random);
                    setResult(PayPwdGuideSetActivity.RESULT_RESET_PAY_PWD, intent);
                    finish();
                } else {
                    showToast(errorMsg);
                    LogUtil.e("PayPWDModify", errorMsg);
                }
                finish();
            }
        });
    }

    /**
     * 展示确认密码页
     */
    public void initData() {
        if (from == Constants.LOCAL_PAYMENT_FROM_GUIDE_SET) {
            tv_enter_pwd_for.setText(getString(R.string.GuideSetPayPWDPage2_B0));
            btn_save.setText(getString(R.string.GuideSetPayPWDPage2_C0));

        } else if (from == Constants.LOCAL_PAYMENT_FROM_RESET) {
            tv_enter_pwd_for.setText(getString(R.string.ModifyPayPWDPage2_B0));
            btn_save.setText(getString(R.string.ModifyPayPWDPage2_C0));
        }
        tv_user_info.setVisibility(View.GONE);
        tv_enter_pwd.setVisibility(View.GONE);

        initUserInfo();

        btn_save.setOnClickListener(this);
        btn_save.setVisibility(View.VISIBLE);
        btn_save.setEnabled(false);
    }

    private void initUserInfo() {
        UserData userData = ProfileUserUtil.getInstance().getUserData();
        if (userData != null) {
            String nName = userData.getNName();
            String lName = userData.getsLName();
            tv_user_info.setText(nName + "(" + lName + ")");
        }
    }

    @Override
    public void onBackPressed() {
        setResult(PayPwdGuideSetActivity.RESULT_RESET_PAY_PWD_BACK);
        finish();
    }
}

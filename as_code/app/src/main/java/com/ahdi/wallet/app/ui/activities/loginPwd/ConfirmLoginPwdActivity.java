package com.ahdi.wallet.app.ui.activities.loginPwd;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.wallet.app.response.ResetLoginPwdRsp;
import com.ahdi.wallet.app.schemas.AuthSchema;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.cryptor.aes.AesKeyCryptor;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.lib.utils.base.BaseEditTextWatcher;

import org.json.JSONObject;

/**
 * @author zhaohe
 * 确认新登录密码密码页面
 */
public class ConfirmLoginPwdActivity extends AppBaseActivity {

    private EditText et_password;
    private Button btn_confirm;
    private CheckBox cb_show;

    /**
     * 前面接口返回的token
     */
    private String token;
    /**
     * 来源：修改密码引导过来的 忘记密码引导来的
     */
    private int from;
    /**
     * 上一个界面输入的密码
     */
    private String newPwd;

    private LoadingDialog loadingDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntent();
        setContentView(R.layout.activity_confirm_new_login_pwd);
        String titleMsg;
        if (from == Constants.LOCAL_FROM_SET_PWD){
            titleMsg = getString(R.string.SettingLoginPwd_A0);
        }else {
            titleMsg = getString(R.string.NewLoginPWD2_A0);
        }
        initCommonTitle(titleMsg);
        initView();
        ActivityManager.getInstance().addSetPwdActivity(this);
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            token = intent.getStringExtra(Constants.LOCAL_TOKEN_KEY);
            from = intent.getIntExtra(Constants.LOCAL_FROM_KEY, 0);
            newPwd = intent.getStringExtra(Constants.LOCAL_pwd_KEY);
        }
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_tips)).setText(getString(R.string.NewLoginPWD2_B0));
        cb_show = findViewById(R.id.cb_show);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        et_password = findViewById(R.id.et_psw);
        et_password.addTextChangedListener(new BaseEditTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pwd = et_password.getText().toString();
                if (!TextUtils.isEmpty(pwd) && pwd.length() >= 6) {
                    btn_confirm.setEnabled(true);
                } else {
                    btn_confirm.setEnabled(false);
                }
            }
        });
        cb_show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                et_password.setSelection(et_password.getText().toString().length());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_confirm:
                String confirmPwd = et_password.getText().toString();
                if (TextUtils.isEmpty(confirmPwd)) {
                    return;
                }
                closeSoftInput();
                if (!TextUtils.equals(newPwd, confirmPwd)) {
                    ToastUtil.showToastShort(ConfirmLoginPwdActivity.this, getString(R.string.Toast_Q0));
                    return;
                }
                resetPwd(confirmPwd);
                break;
            default:
                break;
        }
    }

    /**
     * 设置密码
     *
     * @param confirmPwd
     */
    private void resetPwd(String confirmPwd) {
        loadingDialog = showLoading();
        int type = Constants.LOCAL_FROM_MODIFY_PWD;
        if (from == Constants.LOCAL_FROM_MODIFY_PWD){
            type = Constants.LOCAL_INTERFACE_MODIFY_LOGIN_PWD;
        }else if (from == Constants.LOCAL_FROM_FORGOT_PWD){
            type = Constants.LOCAL_INTERFACE_RESET_LOGIN_PWD;
        }else if (from == Constants.LOCAL_FROM_SET_PWD){
            type = Constants.LOCAL_INTERFACE_CREATE_LOGIN_PWD;
        }
        AppMain.getInstance().resetLoginPwd(type, ConfirmLoginPwdActivity.this,
                AesKeyCryptor.encodeLoginPwd(confirmPwd), token, new UserSdkCallBack() {
                    @Override
                    public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                        closeLoading(loadingDialog);
                        if (TextUtils.equals(code, UserSdk.LOCAL_PAY_SUCCESS)) {
                            ResetLoginPwdRsp response = ResetLoginPwdRsp.decodeJson(ResetLoginPwdRsp.class, jsonObject);
                            if (response != null) {
                                ToastUtil.showToastShort(ConfirmLoginPwdActivity.this, getString(R.string.Toast_R0));
                                boolean isLogined = !TextUtils.isEmpty(ProfileUserUtil.getInstance().getSID());
                                //保存user
                                GlobalApplication.getApplication().updateUserSchema(response.getUserSchema());
                                AuthSchema auth = response.getAuthSchema();
                                if (auth != null) {
                                    //保存sid
                                    ProfileUserUtil.getInstance().setSID(auth.sid);
                                }
                                if (response.getAccountSchema() != null) {
                                    GlobalApplication.getApplication().updateAccountSchema(response.getAccountSchema());
                                }
                                AppMain.getInstance().onTerminalBind();
                                if (isLogined) {
                                    ActivityManager.getInstance().finishSetPswActivity();
                                } else {
                                    ActivityManager.getInstance().clearSetPswActivity();
                                    ActivityManager.getInstance().openMainActivity(ConfirmLoginPwdActivity.this);
                                }
                            }
                        } else {
                            if (TextUtils.equals(code, Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION)) {
                                ToastUtil.showToastShort(ConfirmLoginPwdActivity.this, errorMsg);
                            } else {
                                showErrorDialog(errorMsg);
                            }
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityManager.getInstance().removeSetPwdActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        loadingDialog = null;
    }
}
package com.ahdi.wallet.app.ui.activities.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.bean.RegisterBean;
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.wallet.app.response.RegisterRsp;
import com.ahdi.wallet.app.response.ResetLoginPwdRsp;
import com.ahdi.wallet.app.schemas.AuthSchema;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.cryptor.aes.AesKeyCryptor;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.widgets.CheckCodeDialogUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.lib.utils.base.BaseEditTextWatcher;
import com.ahdi.wallet.app.ui.activities.userInfo.UserInfoSetActivity;

import org.json.JSONObject;

/**
 * Date: 2018/1/2 下午3:33
 * Author: kay lau
 * Description:
 */
public class SetLoginPwdActivity extends AppBaseActivity {

    private String phoneNum, verifyCode, token, agreeVer, slname;
    private EditText et_password;
    private String desc;
    private ImageView iv_eye_pwd;
    private boolean isShowPwd = false;
    private int from;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData();
        if (from == Constants.LOCAL_FROM_SET_PWD){
            setContentView(R.layout.activity_settings_set_login_pwd);
            initCommonTitle(getString(R.string.SettingLoginPwd_A0));
        }else {
            setContentView(R.layout.activity_set_login_pwd);
            initCommonTitle(getString(R.string.SignUpSetPWD_A0));
        }
        initView();
    }

    private void initView() {
        Button btn = findViewById(R.id.set_login_pwd_btn_next);
        btn.setEnabled(false);
        btn.setOnClickListener(this);

        TextView tv_tip_text_phone = findViewById(R.id.tv_tip_text_phone);
        if (!TextUtils.isEmpty(desc)) {
            tv_tip_text_phone.setText(desc);
        } else if (!TextUtils.isEmpty(slname)) {
            if (from == Constants.LOCAL_FROM_SET_PWD){
                tv_tip_text_phone.setText(Html.fromHtml(getString(R.string.SettingLoginPwd_C0, slname)));
            }else {
                tv_tip_text_phone.setText(slname);
            }
        }

        iv_eye_pwd = findViewById(R.id.iv_eye_pwd);
        iv_eye_pwd.setOnClickListener(this);

        et_password = findViewById(R.id.et_set_login_pwd);
        et_password.setFilters(new InputFilter[]{new InputFilter.LengthFilter(24)});
        et_password.addTextChangedListener(new BaseEditTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pwd = et_password.getText().toString();
                if (!TextUtils.isEmpty(pwd)) {
                    et_password.setTextSize(15);
                    if (pwd.length() >= 6) {
                        btn.setEnabled(true);
                    } else {
                        btn.setEnabled(false);
                    }
                } else {
                    et_password.setTextSize(12);
                    btn.setEnabled(false);
                }
            }
        });
    }

    private void initIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            phoneNum = intent.getStringExtra(Constants.PH_KEY);
            verifyCode = intent.getStringExtra(Constants.LOCAL_VERIFY_CODE_KEY);
            desc = intent.getStringExtra(Constants.LOCAL_DESC_KEY);
            token = intent.getStringExtra(Constants.LOCAL_TOKEN_KEY);
            agreeVer = intent.getStringExtra(Constants.LOCAL_VERSION_KEY);
            slname = intent.getStringExtra(Constants.LOCAL_SLNAME_KEY);
            from = intent.getIntExtra(Constants.LOCAL_FROM_KEY,0);
        }
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.set_login_pwd_btn_next:
                if (from == Constants.LOCAL_FROM_SET_PWD){
                   toSetLoginPwd(et_password.getText().toString());
                }else {
                    toRegister();
                }
                break;
            case R.id.iv_eye_pwd:
                pwdShowSwitch();
                break;
            default:
                break;
        }
    }

    private void pwdShowSwitch() {
        if (isShowPwd) {
            isShowPwd = false;
            et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            iv_eye_pwd.setImageResource(R.mipmap.common_eye_show_black);
        } else {
            isShowPwd = true;
            et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            iv_eye_pwd.setImageResource(R.mipmap.common_eye_close_black);
        }
        String psd = et_password.getText().toString();
        if (!TextUtils.isEmpty(psd)) {
            et_password.setSelection(psd.length());
        }
    }

    private void toRegister() {
        register(et_password.getText().toString());
    }

    /**
     * 注册
     *
     * @param pwd
     */
    private void register(String pwd) {
        RegisterBean bean = buildReg(pwd);
        LoadingDialog loadingDialog = showLoading();
        AppMain.getInstance().register(SetLoginPwdActivity.this, bean, new UserSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                RegisterRsp resp = RegisterRsp.decodeJson(RegisterRsp.class, jsonObject);
                if (resp != null && TextUtils.equals(code, UserSdk.LOCAL_PAY_SUCCESS)) {
                    AuthSchema auth = resp.getAuth();
                    if (auth != null) {
                        ProfileUserUtil.getInstance().setSID(auth.sid);
                    }
                    //保存user
                    GlobalApplication.getApplication().updateUserSchema(resp.getUser());
                    if (resp.getAccountSchema() != null) {
                        GlobalApplication.getApplication().updateAccountSchema(resp.getAccountSchema());
                    }
                    AppMain.getInstance().onTerminalBind();
                    closeSoftInput();
                    // 注册完成, 引导设置用户信息
                    startActivity(new Intent(SetLoginPwdActivity.this, UserInfoSetActivity.class));
                } else if (TextUtils.equals(code, Constants.RET_CODE_PP010)) {
                    CheckCodeDialogUtil.getInstance()
                            .showDialog(SetLoginPwdActivity.this, errorMsg, "0");
                } else {
                    showToast(errorMsg);
                }
            }
        });
    }

    private void toSetLoginPwd(String pwd){
        LoadingDialog loadingDialog = showLoading();
        AppMain.getInstance().resetLoginPwd(Constants.LOCAL_INTERFACE_CREATE_LOGIN_PWD, SetLoginPwdActivity.this,
             AesKeyCryptor.encodeLoginPwd(pwd), token, new UserSdkCallBack() {
                @Override
                public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                    closeLoading(loadingDialog);
                    if (TextUtils.equals(code, UserSdk.LOCAL_PAY_SUCCESS)) {
                        ResetLoginPwdRsp response = ResetLoginPwdRsp.decodeJson(ResetLoginPwdRsp.class, jsonObject);
                        if (response != null) {
                            ToastUtil.showToastShort(SetLoginPwdActivity.this, getString(R.string.Toast_R0));
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
                            //更新是否有登录密码状态
                            ProfileUserUtil.getInstance().setHasPwd(true);
                            finish();
                        }
                    } else {
                        if (TextUtils.equals(code, Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION)) {
                            ToastUtil.showToastShort(SetLoginPwdActivity.this, errorMsg);
                        } else {
                            showErrorDialog(errorMsg);
                        }
                    }
                }
        });
    }

    @NonNull
    private RegisterBean buildReg(String pwd) {
        RegisterBean bean = new RegisterBean();
        bean.setLoginType(Constants.PH_KEY);
        bean.setLoginName(phoneNum);
        bean.setToken(token);
        bean.setLoginPassword(AesKeyCryptor.encodeLoginPwd(pwd));
        bean.setvCode(verifyCode);
        bean.setAgreeVer(agreeVer);
        return bean;
    }

    @Override
    public void onBackPressed() {
        closeSoftInput();
        finish();
    }
}
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

import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.wallet.R;
import com.ahdi.lib.utils.base.BaseEditTextWatcher;
import com.ahdi.wallet.app.ui.activities.loginPwd.ConfirmLoginPwdActivity;

/**
 * @author zhaohe
 * 输入新登录密码密码页面
 */
public class NewLoginPwdActivity extends AppBaseActivity {

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
     * 手机号码
     */
    private String phone;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntent(getIntent());
        setContentView(R.layout.activity_new_login_pwd);
        String titleMsg;
        if (from == Constants.LOCAL_FROM_SET_PWD) {
            titleMsg = getString(R.string.SettingLoginPwd_A0);
        } else {
            titleMsg = getString(R.string.NewLoginPWD1_A0);
        }
        initCommonTitle(titleMsg);
        initView();
        ActivityManager.getInstance().addSetPwdActivity(this);
    }

    private void initIntent(Intent intent) {
        if (intent != null) {
            token = intent.getStringExtra(Constants.LOCAL_TOKEN_KEY);
            phone = intent.getStringExtra(Constants.LOCAL_PAYMENT_PHONE);
            from = intent.getIntExtra(Constants.LOCAL_FROM_KEY, 0);
        }
    }

    private void initView() {
        if (from == Constants.LOCAL_FROM_SET_PWD) {
            ((TextView) findViewById(R.id.tv_tips1)).setText(getString(R.string.NewLoginPWD1_B0));
        } else {
            ((TextView) findViewById(R.id.tv_tips1)).setText(getString(R.string.NewLoginPWD1_B0));
        }

        ((TextView) findViewById(R.id.tv_tips2)).setText(phone);
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
                String psw = et_password.getText().toString();
                if (TextUtils.isEmpty(psw)) {
                    return;
                }
                closeSoftInput();
                confirmPsw(psw);
                break;
            default:
                break;
        }
    }

    private void confirmPsw(String psw) {
        Intent intent = new Intent(this, ConfirmLoginPwdActivity.class);
        intent.putExtra(Constants.LOCAL_TOKEN_KEY, token);
        intent.putExtra(Constants.LOCAL_FROM_KEY, from);
        intent.putExtra(Constants.LOCAL_pwd_KEY, psw);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityManager.getInstance().removeSetPwdActivity(this);
    }
}
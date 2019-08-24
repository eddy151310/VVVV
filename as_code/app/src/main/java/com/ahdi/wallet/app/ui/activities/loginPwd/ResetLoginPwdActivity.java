package com.ahdi.wallet.app.ui.activities.loginPwd;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
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
import com.ahdi.wallet.app.response.CheckLoginPwdRsp;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.cryptor.aes.AesKeyCryptor;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.widgets.CheckCodeDialogUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.lib.utils.base.BaseEditTextWatcher;

import org.json.JSONObject;

/**
 * @author zhaohe
 * 重置登录密码页面 --- 输入原始密码页面
 */
public class ResetLoginPwdActivity extends AppBaseActivity {

    private EditText et_password;
    private Button btn_confirm;
    private CheckBox cb_show;

    private LoadingDialog loadingDialog = null;
    private String areaCode = ConfigCountry.KEY_AREA_CODE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_reset_login_password);
        initCommonTitle(getString(R.string.OriginalPWD_A0));
        initView();
        ActivityManager.getInstance().addSetPwdActivity(this);
    }

    private void initView() {
        String lName = "";
        if (ProfileUserUtil.getInstance().getUserData() != null) {
            lName = ProfileUserUtil.getInstance().getUserData().getsLName();
        }
        ((TextView) findViewById(R.id.tv_tips1)).setText(Html.fromHtml(getString(R.string.OriginalPWD_B0, lName)));
        ((TextView) findViewById(R.id.tv_tips2)).setText(getString(R.string.OriginalPWD_F0));
        cb_show = findViewById(R.id.cb_show);
        cb_show.setChecked(true);

        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        TextView tv_forget_pwd = findViewById(R.id.tv_forget_pwd);
        tv_forget_pwd.setOnClickListener(this);
        areaCode = AppGlobalUtil.getInstance().getString(ResetLoginPwdActivity.this, Constants.LOCAL_AREA_CODE_KEY);
        if (TextUtils.equals(areaCode, ConfigCountry.KEY_AREA_CODE)) {
            tv_forget_pwd.setVisibility(View.VISIBLE);
        } else {
            tv_forget_pwd.setVisibility(View.INVISIBLE);
        }
        et_password = findViewById(R.id.et_psw);
        et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_password.addTextChangedListener(new BaseEditTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pwd = et_password.getText().toString().trim();
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
                checkPwd();
                break;
            case R.id.tv_forget_pwd:
                goForgetPwd();
                break;
            default:
                break;
        }
    }

    private void goForgetPwd() {
        Intent forgetPwd = new Intent(ResetLoginPwdActivity.this, ForgetPwdActivity.class);
        startActivity(forgetPwd);
    }

    /**
     * 检测密码
     */
    private void checkPwd() {
        String psw = et_password.getText().toString();
        if (TextUtils.isEmpty(psw)) {
            return;
        }
        closeSoftInput();
        loadingDialog = showLoading();
        AppMain.getInstance().checkLoginPwd(ResetLoginPwdActivity.this, AesKeyCryptor.encodeLoginPwd(psw), new UserSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                if (TextUtils.equals(code, UserSdk.LOCAL_PAY_SUCCESS)) {
                    CheckLoginPwdRsp response = CheckLoginPwdRsp.decodeJson(CheckLoginPwdRsp.class, jsonObject);
                    if (response != null && !TextUtils.isEmpty(response.getToken())) {
                        setNewPsw(response.getToken());
                    }
                } else if (TextUtils.equals(code, Constants.RET_CODE_PP004) || TextUtils.equals(code, Constants.RET_CODE_PP013)) {
                    String lname = AppGlobalUtil.getInstance().getString(ResetLoginPwdActivity.this, Constants.LOCAL_LNAME_KEY);
                    String loginName = "";
                    if (!TextUtils.isEmpty(areaCode)) {
                        loginName = areaCode.concat(lname);
                    }
                    CheckCodeDialogUtil.getInstance().showDialog(ResetLoginPwdActivity.this, errorMsg, code, loginName);
                } else {
                    ToastUtil.showToastShort(ResetLoginPwdActivity.this, errorMsg);
                }
            }
        });
    }

    /**
     * 设置新密码
     *
     * @param token
     */
    private void setNewPsw(String token) {
        Intent intent = new Intent(ResetLoginPwdActivity.this, NewLoginPwdActivity.class);
        intent.putExtra(Constants.LOCAL_TOKEN_KEY, token);
        intent.putExtra(Constants.LOCAL_FROM_KEY, Constants.LOCAL_FROM_MODIFY_PWD);
        String lName = "";
        if (ProfileUserUtil.getInstance().getUserData() != null) {
            lName = ProfileUserUtil.getInstance().getUserData().getsLName();
        }
        intent.putExtra(Constants.LOCAL_PAYMENT_PHONE, lName);
        startActivity(intent);
        ActivityManager.getInstance().removeSetPwdActivity(this);
        finish();
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
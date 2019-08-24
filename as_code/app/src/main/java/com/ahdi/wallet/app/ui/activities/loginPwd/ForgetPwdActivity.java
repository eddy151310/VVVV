package com.ahdi.wallet.app.ui.activities.loginPwd;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.bean.UserData;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.utils.StringUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.ui.activities.login.LoginActivity;
import com.ahdi.wallet.app.ui.adapters.listener.PhoneTextWatcher;

import org.json.JSONObject;

/**
 * @author zhaohe
 * 忘记密码流程  -----输入手机号码页面
 */
public class ForgetPwdActivity extends AppBaseActivity {

    private EditText et_phone;
    private Button btn_confirm;
    /**
     * 前一个页面传过来的手机号码
     */
    private String prePhoneNum = "";

    private String areaCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        areaCode = AppGlobalUtil.getInstance().getString(this, Constants.LOCAL_AREA_CODE_KEY);
        if (TextUtils.isEmpty(areaCode)) {
            areaCode = ConfigCountry.KEY_AREA_CODE;
        }
        setContentView(R.layout.activity_input_phone_number);
        initIntent(getIntent());
        initTitleView();
        initView();
        initData();
        if (!TextUtils.isEmpty(GlobalApplication.getApplication().getSID())) {
            ActivityManager.getInstance().addSetPwdActivity(this);
        }
    }

    private void initIntent(Intent intent) {
        if (intent != null) {
            prePhoneNum = intent.getStringExtra(Constants.LOCAL_PAYMENT_PHONE);
        }
        //如果是已经登录的状态，直接跳转到短信验证码页面
        UserData userData = ProfileUserUtil.getInstance().getUserData();
        if (userData != null && !TextUtils.isEmpty(ProfileUserUtil.getInstance().getSID())) {
            onNext(userData.getLName());
            ActivityManager.getInstance().removeSetPwdActivity(this);
            finish();
        }
    }

    private void initTitleView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.LoginAccount_A0));
        View backView = findViewById(R.id.btn_back);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCanClick()) {
                    return;
                }
                closeSoftInput();
                setResult(LoginActivity.RESULT_CLOSE_FORGOT);
                finish();
            }
        });
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_62)).setText(ConfigCountry.KEY_ADD_AREA_CODE);
        et_phone = findViewById(R.id.et_phone);
        btn_confirm = findViewById(R.id.btn_confirm);
        ((TextView) findViewById(R.id.tv_country)).setText(ConfigCountry.KEY_COUNTRY);
    }

    private void initData() {
        et_phone.setText(prePhoneNum);
        checkPhoneLength(prePhoneNum);
        btn_confirm.setOnClickListener(this);
        et_phone.addTextChangedListener(new PhoneTextWatcher(et_phone, areaCode) {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                String account = et_phone.getText().toString();
                checkPhoneLength(account);
            }
        });
    }

    private void checkPhoneLength(String phone) {
        if (AppGlobalUtil.getInstance().checkPhoneNum(areaCode, phone)) {
            btn_confirm.setEnabled(true);

        } else {
            btn_confirm.setEnabled(false);
        }
    }

    private void checkPhoneNum() {
        String phoneNum = et_phone.getText().toString();
        if (phoneNum.startsWith(ConfigCountry.PHONE_PREFIX_8)
                || phoneNum.startsWith(ConfigCountry.PHONE_PREFIX_08)) {
            closeSoftInput();
            queryRegister(StringUtil.formatPhoneNumber(phoneNum));
        } else {
            showToast(getString(R.string.Toast_E0));
        }
    }

    /**
     * 查询是否已经注册
     *
     * @param phoneNum
     */
    private void queryRegister(String phoneNum) {
        LoadingDialog loadingDialog = showLoading();
        AppMain.getInstance().queryRegister(ForgetPwdActivity.this, phoneNum, 1, new UserSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                if (TextUtils.equals(code, UserSdk.LOCAL_PAY_SUCCESS)) {
                    onNext(phoneNum);
                } else {
                    showToast(errorMsg);
                }
            }
        });
    }

    private void onNext(String phoneNum) {
        Intent intent = new Intent(this, ForgetPwdVerifyActivity.class);
        if (phoneNum.startsWith(ConfigCountry.PHONE_PREFIX_8)
                || phoneNum.startsWith(ConfigCountry.PHONE_PREFIX_08)) {
            intent.putExtra(Constants.PH_KEY, StringUtil.formatPhoneNumber(phoneNum));

        } else {
            intent.putExtra(Constants.PH_KEY, phoneNum);
        }
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        if (v.getId() == R.id.btn_confirm) {
            checkPhoneNum();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityManager.getInstance().removeActivity(this);
    }
}

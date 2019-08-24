package com.ahdi.wallet.app.ui.activities.loginPwd;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahdi.wallet.app.sdk.OtherSdk;
import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.wallet.app.response.VerifyCodeRsp;
import com.ahdi.lib.utils.utils.CountdownUtil;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.CheckCodeDialogUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.callback.AppConfigCallBack;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.lib.utils.base.BaseEditTextWatcher;
import com.ahdi.wallet.util.AppConfigHelper;

import org.json.JSONObject;

/**
 * @author zhaohe
 * 忘记密码流程 ----- 短信验证码界面
 */
public class ForgetPwdVerifyActivity extends AppBaseActivity {

    private static final String TAG = "ForgetPwdVerifyAct";
    private LoadingDialog loadingDialog = null;

    private LinearLayout ll_sent_time;
    private EditText et_verify_code;
    private CountdownUtil countdownUtil;
    private TextView tv_sendTime, tv_tip_text_phone, tv_phone_no_message;
    private Button btn_confirm;

    private String phoneNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData();
        setContentView(R.layout.activity_forget_pwd_code);
        initCommonTitle(getString(R.string.ForgetPwdVerify_A0));
        initView();
        initData();
        if (!TextUtils.isEmpty(GlobalApplication.getApplication().getSID())) {
            ActivityManager.getInstance().addSetPwdActivity(this);
        }
    }

    private void initIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            phoneNum = intent.getStringExtra(Constants.PH_KEY);
        }
    }

    private void initView() {
        ll_sent_time = findViewById(R.id.ll_sent_time_bg);
        tv_sendTime = findViewById(R.id.tv_send_code);
        tv_tip_text_phone = findViewById(R.id.tv_tip_text_phone);
        tv_phone_no_message = findViewById(R.id.tv_phone_no_message);
        tv_phone_no_message.setOnClickListener(this);
        btn_confirm = findViewById(R.id.btn_confirm);

        tv_sendTime.setText(getString(R.string.ForgetPwdVerify_D0));
        et_verify_code = findViewById(R.id.et_verify_code);
        et_verify_code.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        et_verify_code.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_verify_code.setTypeface(Typeface.DEFAULT);
        et_verify_code.addTextChangedListener(new BaseEditTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String code = et_verify_code.getText().toString();
                if (!TextUtils.isEmpty(code)) {
                    if (code.length() >= 4) {
                        btn_confirm.setEnabled(true);
                    } else {
                        btn_confirm.setEnabled(false);
                    }
                } else {
                    btn_confirm.setEnabled(false);
                }
            }
        });
        ll_sent_time.setOnClickListener(this);
        btn_confirm.setEnabled(false);
        btn_confirm.setOnClickListener(this);
    }

    private void initData() {
        onSendVerifyCode();
    }

    /**
     * 倒计时
     *
     * @param count
     */
    public void startCount(int count) {
        stopCount();
        if (countdownUtil == null) {
            countdownUtil = new CountdownUtil();
        }
        countdownUtil.startCount(count, new CountdownUtil.CountDownCallback() {

            @Override
            public void onCountDowning(int count) {
                setSendBtnEnabled(false, count);
            }

            @Override
            public void onComplete() {
                setSendBtnEnabled(true);
            }
        });
    }

    private void stopCount() {
        if (countdownUtil != null) {
            countdownUtil.destroyTimer();
        }
    }


    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.btn_confirm) {
            // 短信验证码校验
            onCheckVerifyCode();

        } else if (id == R.id.ll_sent_time_bg) {
            // 发送短信验证码, 每次点击: 返回倒计时具体时间 置灰显示不可点击
            onSendVerifyCode();
        } else if (id == R.id.tv_phone_no_message) {
            openFAQUrl();
        }
    }

    /**
     * 下发短信验证码
     */
    private void onSendVerifyCode() {
        LoadingDialog loadingDialog = showLoading();
        // 接口响应倒计时时间, 开始置灰倒计时
        AppMain.getInstance().sendVCodeForLoginPwd(this, phoneNum, new UserSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                VerifyCodeRsp response = VerifyCodeRsp.decodeJson(VerifyCodeRsp.class, jsonObject);
                if (response != null && TextUtils.equals(code, UserSdk.LOCAL_PAY_SUCCESS)) {
                    if (response.getVerifyCodeSchema() != null) {
                        tv_tip_text_phone.setText(response.getVerifyCodeSchema().Desc);
                        startCount(response.getVerifyCodeSchema().Wait);
                    } else {
                        LogUtil.e(TAG, "response.getVerifyCodeSchema() = null");
                    }
                } else if (TextUtils.equals(code, UserSdk.LOCAL_PAY_SYSTEM_EXCEPTION)
                        || TextUtils.equals(code, UserSdk.LOCAL_PAY_NETWORK_EXCEPTION)) {
                    showToast(errorMsg);
                } else {
                    if (response != null && response.getVerifyCodeSchema() != null) {
                        if (!TextUtils.isEmpty(response.getVerifyCodeSchema().Desc)) {
                            tv_tip_text_phone.setText(response.getVerifyCodeSchema().Desc);
                        }
                        if (response.getVerifyCodeSchema().Remain <= 0) {
                            tv_sendTime.setText(getString(R.string.ForgetPwdVerify_D0));
                            tv_sendTime.setEnabled(false);
                            ll_sent_time.setEnabled(false);
                        } else {
                            startCount(response.getVerifyCodeSchema().Wait + 1);
                        }
                    }
                    CheckCodeDialogUtil.getInstance().showDialog(ForgetPwdVerifyActivity.this, errorMsg, "0");
                }
            }
        });
    }

    /**
     * 短信验证码校验
     */
    private void onCheckVerifyCode() {
        String verifyCode = et_verify_code.getText().toString();
        LoadingDialog loadingDialog = showLoading();
        AppMain.getInstance().checkVCodeForLoginPwd(this, phoneNum, verifyCode, new UserSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                VerifyCodeRsp response = VerifyCodeRsp.decodeJson(VerifyCodeRsp.class, jsonObject);

                if (response != null && TextUtils.equals(code, UserSdk.LOCAL_PAY_SUCCESS)) {
                    Intent intent = new Intent(ForgetPwdVerifyActivity.this, NewLoginPwdActivity.class);
                    if (response.getVerifyCodeSchema() != null) {
                        intent.putExtra(Constants.LOCAL_PAYMENT_PHONE, response.getVerifyCodeSchema().Desc);
                    } else {
                        intent.putExtra(Constants.LOCAL_PAYMENT_PHONE, phoneNum);
                    }
                    intent.putExtra(Constants.LOCAL_FROM_KEY, Constants.LOCAL_FROM_FORGOT_PWD);
                    intent.putExtra(Constants.LOCAL_TOKEN_KEY, response.token);
                    startActivity(intent);
                } else {
                    ToastUtil.showToastShort(ForgetPwdVerifyActivity.this, errorMsg);
                }
            }
        });
    }

    /**
     * 更新配置
     */
    private void openFAQUrl() {
        loadingDialog = showLoading();
        AppMain.getInstance().appConfig(ForgetPwdVerifyActivity.this, new AppConfigCallBack() {
            @Override
            public void onResult(String Code, String errorMsg) {
                closeLoading(loadingDialog);
                if (TextUtils.equals(Code, OtherSdk.LOCAL_SUCCESS)) {
                    openWebCommonActivity(AppConfigHelper.getInstance().getrPwdServiceUrl());
                } else {
                    showToast(errorMsg);
                }
            }
        });
    }

    private void setSendBtnEnabled(boolean enabled) {
        setSendBtnEnabled(enabled, 0);
    }

    private void setSendBtnEnabled(boolean enabled, int count) {
        ll_sent_time.setEnabled(enabled);
        tv_sendTime.setEnabled(enabled);
        if (enabled) {
            tv_sendTime.setText(getString(R.string.ForgetPwdVerify_D0));
        } else {
            tv_sendTime.setText(getString(R.string.ForgetPwdVerify_D0) + " (" + count + "s) ");
        }

    }

    /**
     * 清理倒计时
     */
    private void destroyCountDown() {
        if (countdownUtil != null) {
            countdownUtil.destroyTimer();
            countdownUtil = null;
        }
    }

    @Override
    public void onBackPressed() {
        closeSoftInput();
        ActivityManager.getInstance().removeActivity(this);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyCountDown();
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        loadingDialog = null;
    }
}

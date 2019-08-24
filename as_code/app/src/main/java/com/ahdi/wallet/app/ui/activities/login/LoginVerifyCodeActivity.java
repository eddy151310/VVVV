package com.ahdi.wallet.app.ui.activities.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ahdi.wallet.app.sdk.OtherSdk;
import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.bean.LoginBean;
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.wallet.app.response.LoginRsp;
import com.ahdi.wallet.app.response.VerifyCodeRsp;
import com.ahdi.wallet.app.schemas.AuthSchema;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.fingerprint.FingerprintSDK;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.CountdownUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.utils.TerminalIdUtil;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;
import com.ahdi.lib.utils.widgets.CheckCodeDialogUtil;
import com.ahdi.lib.utils.widgets.dialog.DialogSigupQR;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.callback.AppConfigCallBack;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.lib.utils.base.BaseEditTextWatcher;
import com.ahdi.wallet.app.ui.activities.AppMainActivity;
import com.ahdi.wallet.app.ui.activities.biometric.GuideTouchIDUnlockActivity;
import com.ahdi.wallet.util.AppConfigHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Date: 2018/4/23 下午2:00
 * Author: kay lau
 * Description: 登录: 短信验证码界面
 */
public class LoginVerifyCodeActivity extends AppBaseActivity {

    public static final String TAG = "LoginVerifyCode";
    private EditText et_verify_code;
    private CountdownUtil countdownUtil;
    private TextView tv_sendCode, tv_tip_slname;
    private Button btn_next;
    private String phoneNum;
    private TextView tv_code_1, tv_code_2, tv_code_3, tv_code_4, tv_code_5, tv_code_6;
    private ScrollView scrollView;
    private int masterLayoutBottom;
    private ViewTreeObserver.OnGlobalLayoutListener listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData(getIntent());
        setContentView(R.layout.layout_activity_login_verify_code);
        initTitleView(getString(R.string.LoginVer_A0));
        initView();
        onSendVerifyCode();

    }

    private void initTitleView(String title) {
        View view = findViewById(R.id.verify_code_title);
        //初始化控件
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(title);
        Button btn_backView = view.findViewById(R.id.btn_back);
        btn_backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCanClick()) {
                    return;
                }
                finish();
            }
        });

        tv_title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("TID", TerminalIdUtil.getTerminalID());
                    jsonObject.put("LName", phoneNum);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String encode = Base64.encodeToString(jsonObject.toString().getBytes(), Base64.NO_WRAP);
                String content = "OTP!!0001".concat(encode);
                new DialogSigupQR
                        .Builder(LoginVerifyCodeActivity.this)
                        .setContent(content)
                        .setNegativeButton(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .showDialog();
                return true;
            }
        });

        View btn_next = view.findViewById(R.id.btn_next);
        btn_next.setVisibility(View.INVISIBLE);
    }

    private void initIntentData(Intent intent) {
        if (intent != null) {
            phoneNum = intent.getStringExtra(Constants.PH_KEY);
        }
    }

    private void initView() {
        getMasterLayoutBottom();
        findViewById(R.id.tv_phone_no_message).setOnClickListener(this);

        tv_tip_slname = findViewById(R.id.tv_tip_text_phone);
        btn_next = findViewById(R.id.verify_btn_next);
        btn_next.setEnabled(false);
        btn_next.setOnClickListener(this);
        scrollView = findViewById(R.id.scrollView);
        findViewById(R.id.rl_edt_opt).setOnClickListener(this);

        initEdtOTP();
        initSoftKeyboard();
    }

    private void initEdtOTP() {
        et_verify_code = findViewById(R.id.edt_otp);
        tv_code_1 = findViewById(R.id.tv_code_1);
        tv_code_2 = findViewById(R.id.tv_code_2);
        tv_code_3 = findViewById(R.id.tv_code_3);
        tv_code_4 = findViewById(R.id.tv_code_4);
        tv_code_5 = findViewById(R.id.tv_code_5);
        tv_code_6 = findViewById(R.id.tv_code_6);
        tv_sendCode = findViewById(R.id.tv_send_code);

        tv_sendCode.setText(getString(R.string.LoginVer_D0));
        tv_sendCode.setOnClickListener(this);

        et_verify_code.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        et_verify_code.addTextChangedListener(new BaseEditTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String code = et_verify_code.getText().toString();
                if (!TextUtils.isEmpty(code) && code.length() >= 6) {
                    btn_next.setEnabled(true);
                } else {
                    btn_next.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                showInputVerifyCode(s);
            }
        });
    }

    private void showInputVerifyCode(Editable s) {
        tv_code_1.setText("");
        tv_code_2.setText("");
        tv_code_3.setText("");
        tv_code_4.setText("");
        tv_code_5.setText("");
        tv_code_6.setText("");
        switch (s.length()) {
            case 6:
                tv_code_6.setText(s.subSequence(5, 6));
            case 5:
                tv_code_5.setText(s.subSequence(4, 5));
            case 4:
                tv_code_4.setText(s.subSequence(3, 4));
            case 3:
                tv_code_3.setText(s.subSequence(2, 3));
            case 2:
                tv_code_2.setText(s.subSequence(1, 2));
            case 1:
                tv_code_1.setText(s.subSequence(0, 1));
            default:
                break;
        }
    }

    private void getMasterLayoutBottom() {
        LinearLayout master_layout = findViewById(R.id.master_layout);
        ViewTreeObserver viewTreeObserver = master_layout.getViewTreeObserver();
        removeViewTreeObserver(viewTreeObserver, listener);
        listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                masterLayoutBottom = master_layout.getBottom();
            }
        };
        viewTreeObserver.addOnGlobalLayoutListener(listener);
    }

    private void initSoftKeyboard() {
        ViewTreeObserver viewTreeObserver = et_verify_code.getViewTreeObserver();
        if (viewTreeObserver != null) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    removeViewTreeObserver(viewTreeObserver, this);
                    android.graphics.Rect r = new android.graphics.Rect();
                    et_verify_code.getWindowVisibleDisplayFrame(r);
                    int screenHeight = et_verify_code.getRootView().getHeight();
                    int heightDifference = screenHeight - (r.bottom);
                    if (heightDifference > 200) {
                        //软键盘显示
                        scrollView.scrollTo(0, masterLayoutBottom);
                    } else {
                        //软键盘隐藏
                        scrollView.scrollTo(0, 0);
                    }
                }
            });
        }
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
        if (id == R.id.verify_btn_next) {
            // otp登录
            onOTPLogin();

        } else if (id == R.id.tv_send_code) {
            // 发送短信验证码, 每次点击: 返回倒计时具体时间 置灰显示不可点击
            onSendVerifyCode();

        } else if (id == R.id.tv_phone_no_message) {
            openFAQUrl();

        } else if (id == R.id.rl_edt_opt) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(et_verify_code, 0);
            }
        }
    }

    /**
     * 下发短信验证码
     */
    private void onSendVerifyCode() {
        LoadingDialog loadingDialog = showLoading();
        // 接口响应倒计时时间, 开始置灰倒计时
        AppMain.getInstance().sendVCodeForLogin(this, phoneNum, new UserSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                VerifyCodeRsp response = VerifyCodeRsp.decodeJson(VerifyCodeRsp.class, jsonObject);
                if (response != null && TextUtils.equals(code, UserSdk.LOCAL_PAY_SUCCESS)) {
                    if (response.getVerifyCodeSchema() != null) {
                        tv_tip_slname.setText(response.getVerifyCodeSchema().Desc);
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
                            tv_tip_slname.setText(response.getVerifyCodeSchema().Desc);
                        }
                        if (response.getVerifyCodeSchema().Remain <= 0) {
                            tv_sendCode.setText(getString(R.string.LoginVer_D0));
                            tv_sendCode.setEnabled(false);
                        } else {
                            startCount(response.getVerifyCodeSchema().Wait + 1);
                        }
                    }
                    showToast(errorMsg);
                }
            }
        });
    }

    /**
     * otp登录
     */
    private void onOTPLogin() {
        String verifyCode = et_verify_code.getText().toString();
        LoginBean loginBean = new LoginBean();
        loginBean.setLoginName(phoneNum);
        loginBean.setLoginPassword(verifyCode);
        loginBean.setLoginType(Constants.LS_KEY);
        LoadingDialog loadingDialog = showLoading();
        AppMain.getInstance().goToLogin(this, loginBean, new UserSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                handleLoginResponse(code, errorMsg, jsonObject, phoneNum);
            }
        });
    }

    private void handleLoginResponse(String code, String errorMsg, JSONObject jsonObject, String phoneNum) {

        LoginRsp resp = LoginRsp.decodeJson(LoginRsp.class, jsonObject);
        if (resp != null && TextUtils.equals(code, UserSdk.LOCAL_PAY_SUCCESS)) {
            //保存user
            GlobalApplication.getApplication().updateUserSchema(resp.getUser());
            AuthSchema auth = resp.getAuth();
            if (auth != null) {
                //保存sid
                ProfileUserUtil.getInstance().setSID(auth.sid);
            }
            if (resp.getAccount() != null) {
                GlobalApplication.getApplication().updateAccountSchema(resp.getAccount());
            }

            String LName = AppGlobalUtil.getInstance().getLName(this);
            if (!TouchIDStateUtil.isSkipGuideUnlock(this, LName)
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && FingerprintSDK.isSupport(this)
                    && FingerprintSDK.isHasFingerprints(this)
                    && !TouchIDStateUtil.isStartTouchIDUnlock(this, LName)) {
                // 引导设置指纹解锁登录
                Intent intent = new Intent(this, GuideTouchIDUnlockActivity.class);
                startActivityForResult(intent, GuideTouchIDUnlockActivity.OPEN_REQUEST_CODE);

            } else {
                gotoMainActivity();
            }
            AppMain.getInstance().onTerminalBind();

        } else if (TextUtils.equals(code, Constants.RET_CODE_PP001)
                || TextUtils.equals(code, Constants.RET_CODE_PP013)) {
            CheckCodeDialogUtil.getInstance().showDialog(this, errorMsg, code, phoneNum);
        } else {
            showToast(errorMsg);
        }
    }

    /**
     * 跳到App首页
     */
    private void gotoMainActivity() {
        ActivityManager.getInstance().openMainActivity(this);
    }

    private void setSendBtnEnabled(boolean enabled) {
        setSendBtnEnabled(enabled, 0);
    }

    private void setSendBtnEnabled(boolean enabled, int count) {
        tv_sendCode.setEnabled(enabled);
        if (enabled) {
            tv_sendCode.setText(getString(R.string.LoginVer_D0));
        } else {
            tv_sendCode.setText(getString(R.string.LoginVer_E0, String.valueOf(count)));
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
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyCountDown();
    }

    /**
     * 更新配置
     */
    private void openFAQUrl() {
        LoadingDialog loadingDialog = showLoading();
        AppMain.getInstance().appConfig(LoginVerifyCodeActivity.this, new AppConfigCallBack() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GuideTouchIDUnlockActivity.OPEN_REQUEST_CODE
                && resultCode == GuideTouchIDUnlockActivity.CLOSE_RESULT_CODE) {
            openMainActivity(GuideTouchIDUnlockActivity.OPEN_REQUEST_CODE);
            finish();
        }
    }

    private void openMainActivity(int openRequestCode) {
        Intent intent = new Intent(this, AppMainActivity.class);
        intent.putExtra(Constants.LOCAL_FROM_KEY, GuideTouchIDUnlockActivity.TAG);
        startActivityForResult(intent, openRequestCode);
    }
}

package com.ahdi.wallet.app.ui.activities.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ahdi.lib.utils.config.ConfigSP;
import com.ahdi.lib.utils.utils.LBSInstance;
import com.ahdi.lib.utils.utils.PermissionsActivity;
import com.ahdi.wallet.app.sdk.OtherSdk;
import com.ahdi.wallet.app.sdk.PhoneAreaCodeSdk;
import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.bean.LoginBean;
import com.ahdi.wallet.app.bean.PhoneAreaCodeBean;
import com.ahdi.wallet.app.callback.OtherSdkCallBack;
import com.ahdi.wallet.app.callback.PhoneAreaCodeCallBack;
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.wallet.app.response.LoginRsp;
import com.ahdi.wallet.app.response.UserAgreementRsp;
import com.ahdi.wallet.app.schemas.AuthSchema;
import com.ahdi.wallet.app.schemas.UserAgreementSchema;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.GlobalContextWrapper;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.config.ConfigHelper;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.fingerprint.FingerprintSDK;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.CleanConfigUtil;
import com.ahdi.lib.utils.utils.LanguageUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.utils.ScreenAdaptionUtil;
import com.ahdi.lib.utils.utils.StatusBarUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;
import com.ahdi.lib.utils.utils.UMengUtil;
import com.ahdi.lib.utils.widgets.CheckCodeDialogUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.lib.utils.base.BaseEditTextWatcher;
import com.ahdi.wallet.app.ui.aaaa.login.LoginActivity2;
import com.ahdi.wallet.app.ui.activities.AppMainActivity;
import com.ahdi.wallet.app.ui.activities.loginPwd.ForgetPwdActivity;
import com.ahdi.wallet.app.ui.activities.biometric.GuideTouchIDUnlockActivity;
import com.ahdi.wallet.app.ui.activities.other.LanguageActivity;
import com.ahdi.wallet.app.ui.activities.signup.SignUpActivity;
import com.ahdi.wallet.app.ui.adapters.listener.PhoneTextWatcher;
import com.baidu.location.BDLocation;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author admin
 */
public class LoginActivity extends PermissionsActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    // 限制登录密码最小长度
    public static final int PWD_MIN_LENGTH = 6;
    // 限制登录密码最大长度
    public static final int PWD_MAX_LENGTH = 24;

    private String areaCode = ConfigCountry.KEY_AREA_CODE;
    /**
     * 设置语言的请求码
     */
    private static final int REQUEST_SET_LANGUAGE = 101;
    private static final int REQUEST_OPEN_FORGOT = 102;
    public static final int RESULT_CLOSE_FORGOT = 103;
    private String lastLanguage;
    /**
     * 登录按钮
     */
    private Button btn_login, btn_login_otp;
    private EditText edt_loginName, edt_login_pwd, edt_loginName_otp;

    private boolean isShowPwd = true;
    private ImageView iv_eye_pwd, iv_logo;
    private View ll_bottom;

    private LoadingDialog loadingDialog = null;
    private CommonDialog dialog = null;
    private BroadcastReceiver broadcastReceiver = new DialogBroadRec();
    private TextView tv_login_pwd_forget;
    private TextView tv_area_code, tv_area_code_otp;

    private PhoneTextWatcher phoneTextWatcher, phoneTextWatcherOTP;
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = null;
    private LinearLayout ll_pwd, ll_otp;
    private ScrollView scrollView;
    private int otpBottom;
    private ViewTreeObserver.OnGlobalLayoutListener listener;
    private boolean isUnlockSkip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenAdaptionUtil.setCustomDensity(this, getApplication());
        setContentView(R.layout.layout_activity_login);
        getWindow().setBackgroundDrawableResource(R.color.CC_F1F2F6); //代码设置bg 防止键盘弹起 闪黑屏
        StatusBarUtil.setStatusBar(this);
        initTitleView();
        initView();
        testAccount();


        if(!checkPermissions(request_type_lbs)){
            requestPermissions(request_type_lbs);
        }else{
            doAnything(request_type_lbs);
        }


    }

    public void initTitleView() {
        View view = findViewById(R.id.titleLayout);
        view.setBackgroundResource(R.color.CC_00000000);
        view.findViewById(R.id.btn_back).setVisibility(View.GONE);
        //设置语言按钮
        Button btn_language = view.findViewById(R.id.btn_next);
        btn_language.setVisibility(View.VISIBLE);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.dp_10);
        btn_language.setPadding(dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
        btn_language.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size_15));
        btn_language.setTextColor(getResources().getColorStateList(R.color.common_link_text_color_black));
        btn_language.setText(getString(R.string.Login_A0));
        btn_language.setBackgroundResource(R.color.CC_00000000);
        btn_language.setOnClickListener(this);
        btn_language.setVisibility(View.GONE);
    }

    /**
     * 初始化输入框限制
     */
    private void initInputLimit() {
        edt_login_pwd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(PWD_MAX_LENGTH)});
        updateViewState(edt_loginName, edt_login_pwd);

        initLoginNameInputLimit();

        edt_login_pwd.addTextChangedListener(new BaseEditTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateViewState(edt_loginName, edt_login_pwd);
            }
        });
    }

    private void initLoginNameInputLimit() {
        phoneTextWatcher = new PhoneTextWatcher(edt_loginName, areaCode) {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //父类根据输入内容处理输入框的长度
                super.onTextChanged(s, start, before, count);
                updateViewState(edt_loginName, edt_login_pwd);
            }
        };
        //父类根据输入内容处理输入框的长度
        edt_loginName.addTextChangedListener(phoneTextWatcher);

    }

    private void initView() {
        lastLanguage = LanguageUtil.getLanguage(this);

        // 选择国家地区 区号
        findViewById(R.id.fl_select_area_code).setOnClickListener(this);
        findViewById(R.id.fl_select_area_code_otp).setOnClickListener(this);

        tv_area_code = findViewById(R.id.tv_area_code);
        tv_area_code_otp = findViewById(R.id.tv_area_code_otp);

        areaCode = AppGlobalUtil.getInstance().getString(this, Constants.LOCAL_AREA_CODE_KEY);
        if (!TextUtils.isEmpty(areaCode)) {
            tv_area_code.setText(ConfigCountry.KEY_ADD.concat(areaCode));
            tv_area_code_otp.setText(ConfigCountry.KEY_ADD.concat(areaCode));
        } else {
            tv_area_code.setText(ConfigCountry.KEY_ADD_AREA_CODE);
            tv_area_code_otp.setText(ConfigCountry.KEY_ADD_AREA_CODE);
            areaCode = ConfigCountry.KEY_AREA_CODE;
        }

        tv_login_pwd_forget = findViewById(R.id.login_pwd_forget);
        tv_login_pwd_forget.setOnClickListener(this);

        findViewById(R.id.btn_sign_up).setOnClickListener(this);
        iv_logo = findViewById(R.id.iv_logo);

        btn_login_otp = findViewById(R.id.btn_login_otp);
        btn_login_otp.setOnClickListener(this);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        edt_loginName_otp = findViewById(R.id.et_login_phone_number_otp);

        edt_loginName = findViewById(R.id.et_login_phone_number);
        String loginName = getLoginName();
        edt_loginName.setText(loginName);
        edt_loginName_otp.setText(loginName);

        edt_login_pwd = findViewById(R.id.et_login_pwd);
        edt_login_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        iv_eye_pwd = findViewById(R.id.iv_eye_pwd);
        iv_eye_pwd.setOnClickListener(this);

        ll_bottom = findViewById(R.id.ll_bottom);
        scrollView = findViewById(R.id.scrollView);

        ll_pwd = findViewById(R.id.ll_pwd);
        ll_otp = findViewById(R.id.ll_otp);

        // 默认显示otp登录
        showOTPLogin();

        findViewById(R.id.btn_otp).setOnClickListener(this);
        findViewById(R.id.btn_pwd).setOnClickListener(this);

        // 获取滑动到父布局底部的位置.
        getMasterLayoutBottom();

        initSoftKeyboard();

        refreshForgotPwdUI();

        initInputLimit();

        initInputLimitOTP();
    }

    /**
     * 初始化输入框限制OTP登录
     */
    private void initInputLimitOTP() {
        updateViewStateOTP(edt_loginName_otp);
        initLoginNameInputLimitOTP();
    }

    private void initLoginNameInputLimitOTP() {
        phoneTextWatcherOTP = new PhoneTextWatcher(edt_loginName_otp, areaCode) {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //父类根据输入内容处理输入框的长度
                super.onTextChanged(s, start, before, count);
                updateViewStateOTP(edt_loginName_otp);
            }
        };
        //父类根据输入内容处理输入框的长度
        edt_loginName_otp.addTextChangedListener(phoneTextWatcherOTP);
    }

    private void getMasterLayoutBottom() {
        LinearLayout master_layout = findViewById(R.id.master_layout);
        ViewTreeObserver viewTreeObserver = master_layout.getViewTreeObserver();
        removeViewTreeObserver(viewTreeObserver, listener);
        listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                otpBottom = master_layout.getBottom();
            }
        };
        viewTreeObserver.addOnGlobalLayoutListener(listener);
    }

    private String getLoginName() {
        String loginName = AppGlobalUtil.getInstance().getString(this, Constants.LOCAL_LNAME_KEY);
        if (!TextUtils.isEmpty(loginName) && loginName.startsWith(areaCode)) {
            loginName = loginName.replaceFirst(areaCode, "");
        }
        return loginName;
    }

    private void refreshForgotPwdUI() {
        if (TextUtils.equals(areaCode, ConfigCountry.KEY_AREA_CODE)) {
            tv_login_pwd_forget.setVisibility(View.VISIBLE);
        } else {
            tv_login_pwd_forget.setVisibility(View.INVISIBLE);
        }
    }

    private void initSoftKeyboard() {
        ViewTreeObserver viewTreeObserver = edt_login_pwd.getViewTreeObserver();
        // 由于切换语言时 recreate 造成的空指针异常, 改为在此移除监听
        removeViewTreeObserver(viewTreeObserver, globalLayoutListener);
        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                if (edt_login_pwd == null) {
                    return;
                }
                edt_login_pwd.getWindowVisibleDisplayFrame(r);
                int screenHeight = edt_login_pwd.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom);
                if (heightDifference > 200) {
                    //软键盘显示
                    ll_bottom.setVisibility(View.GONE);
                    scrollView.scrollTo(0, otpBottom);
                } else {
                    //软键盘隐藏
                    ll_bottom.setVisibility(View.VISIBLE);
                    scrollView.scrollTo(0, 0);
                }
            }
        };
        if (viewTreeObserver != null) {
            viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener);
        }
    }

    /**
     * 界面初始化完成时, observer.isAlive()=false, 不会移除监听 影响UI线程
     *
     * @param observer
     * @param listener
     */
    private void removeViewTreeObserver(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (observer.isAlive()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                observer.removeOnGlobalLayoutListener(listener);
            } else {
                observer.removeGlobalOnLayoutListener(listener);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isUnlockSkip) {
            ActivityManager.getInstance().finishAllActivity();
            CleanConfigUtil.cleanProfileUserManager();
            registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION_ACCOUNT_OUT));
            UMengUtil.onPageStart(getClass().getSimpleName().intern());
            UMengUtil.onResume(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UMengUtil.onPageEnd(getClass().getSimpleName().intern());
        UMengUtil.onPause(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(GlobalContextWrapper.wrap(newBase, LanguageUtil.getLanguage(newBase)));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id != R.id.iv_eye_pwd && !isCanClick()) {
            return;
        }
        switch (id) {
            case R.id.login_pwd_forget:
                // 忘记密码
                openForgetPwd();
                break;
            case R.id.btn_login:
                // 登录
                Intent intent2 = new Intent(this, LoginActivity2.class);
                startActivity(intent2);
                //onLogin();
                break;
            case R.id.btn_login_otp:
                // otp登录
                onOTPLogin();
                break;
            case R.id.btn_sign_up:
                // 注册
                getUserAgreement();
                break;
            case R.id.iv_eye_pwd:
                // 密码是否显示
                pwdShowSwitch();
                break;
//            case R.id.fl_select_area_code_otp:
//            case R.id.fl_select_area_code:
//                closeSoftInput();
//                openAreaCodeList();
//                break;
//            case R.id.btn_next:
//                switchAppLanguage();
//                break;
            case R.id.btn_otp:
                clearEdtFocus();
                ll_otp.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_from_right));
                ll_pwd.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_to_left));
                String loginName = edt_loginName.getText().toString();
                if (!TextUtils.isEmpty(loginName)) {
                    edt_loginName_otp.setText(loginName);
                }
                showOTPLogin();
                break;
            case R.id.btn_pwd:
                clearEdtFocus();
                ll_pwd.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_from_right));
                ll_otp.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_to_left));
                String loginNameOTP = edt_loginName_otp.getText().toString();
                if (!TextUtils.isEmpty(loginNameOTP)) {
                    edt_loginName.setText(loginNameOTP);
                    edt_login_pwd.setText(edt_login_pwd.getText().toString());
                }
                showPwdLogin();
                break;
            default:
                break;
        }
    }

    private void showOTPLogin() {
        ll_pwd.setVisibility(View.INVISIBLE);
        ll_otp.setVisibility(View.VISIBLE);
    }

    private void showPwdLogin() {
        ll_pwd.setVisibility(View.VISIBLE);
        ll_otp.setVisibility(View.INVISIBLE);
    }

    private void onOTPLogin() {
        closeSoftInput();
        String phoneNum = edt_loginName_otp.getText().toString();
        if (phoneNum.startsWith(ConfigCountry.PHONE_PREFIX_08)
                || phoneNum.startsWith(ConfigCountry.PHONE_PREFIX_8)) {
            String otpLoginName = areaCode.concat(phoneNum);
            LoadingDialog loadingDialog = showLoading();
            AppMain.getInstance().queryRegister(this, otpLoginName, 1, new UserSdkCallBack() {
                @Override
                public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                    closeLoading(loadingDialog);
                    if (TextUtils.equals(code, UserSdk.LOCAL_PAY_SUCCESS)) {
                        openOTPLogin(otpLoginName);
                    } else if (TextUtils.equals(code, Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION)) {
                        showToast(errorMsg);
                    } else {
                        showSingleDialog(errorMsg);
                    }
                }
            });
        } else {
            showToast(getString(R.string.DialogMsg_X0));
        }

    }

    public void showSingleDialog(String errorMsg) {
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

    private void openOTPLogin(String otpLoginName) {
        Intent intent = new Intent(this, LoginVerifyCodeActivity.class);
        intent.putExtra(Constants.PH_KEY, otpLoginName);
        startActivity(intent);
    }

    private void openAreaCodeList() {
        PhoneAreaCodeSdk.selectAreaCode(this, new PhoneAreaCodeCallBack() {
            @Override
            public void onResult(String code, String errorMsg, PhoneAreaCodeBean codeBean) {
                if (codeBean == null) {
                    return;
                }
                if (phoneTextWatcher != null) {
                    phoneTextWatcher.setAreaCode(codeBean.getPhoneAreaCode());
                }
                if (phoneTextWatcherOTP != null) {
                    phoneTextWatcherOTP.setAreaCode(codeBean.getPhoneAreaCode());
                }
                areaCode = codeBean.getPhoneAreaCode();
                tv_area_code.setText(ConfigCountry.KEY_ADD.concat(codeBean.getPhoneAreaCode()));
                tv_area_code_otp.setText(ConfigCountry.KEY_ADD.concat(codeBean.getPhoneAreaCode()));
                refreshForgotPwdUI();
                edt_loginName.setText("");
                edt_login_pwd.setText("");
            }
        });
    }

    private void openSignUp(String version, UserAgreementSchema[] agreementSchemas) {
        closeSoftInput();
        Map<String, String> map = new HashMap<>();
        if (agreementSchemas != null && agreementSchemas.length > 0) {
            for (UserAgreementSchema schema : agreementSchemas) {
                map.put(schema.name, schema.url);
            }
        }
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.putExtra(Constants.LOCAL_VERSION_KEY, version);
        intent.putExtra(Constants.LOCAL_AGREEMENTS_KEY, (Serializable) map);
        startActivity(intent);
    }

    private void openForgetPwd() {
        Intent intent = new Intent(this, ForgetPwdActivity.class);
        intent.putExtra(Constants.LOCAL_PAYMENT_PHONE, edt_loginName.getText().toString());
        startActivityForResult(intent, REQUEST_OPEN_FORGOT);
    }

    private void pwdShowSwitch() {
        if (isShowPwd) {
            isShowPwd = false;
            edt_login_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            iv_eye_pwd.setImageResource(R.mipmap.common_eye_show_black);
        } else {
            isShowPwd = true;
            edt_login_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            iv_eye_pwd.setImageResource(R.mipmap.common_eye_close_black);
        }
        String psd = edt_login_pwd.getText().toString();
        if (!TextUtils.isEmpty(psd)) {
            edt_login_pwd.setSelection(psd.length());
        }
    }

    /**
     * 登录流程
     */
    private void onLogin() {
        closeSoftInput();
        String loginName = edt_loginName.getText().toString();
        String loginPassword = edt_login_pwd.getText().toString();

        if (!TextUtils.isEmpty(loginName) && !TextUtils.isEmpty(loginPassword)) {
            if (TextUtils.equals(areaCode, ConfigCountry.KEY_AREA_CODE)) {
                if (loginName.startsWith(ConfigCountry.PHONE_PREFIX_8)
                        || loginName.startsWith(ConfigCountry.PHONE_PREFIX_08)) {
                    closeSoftInput();
                    toLogin(getLoinBean(areaCode.concat(loginName), loginPassword));
                } else {
                    showToast(getString(R.string.DialogMsg_X0));
                }
            } else {
                closeSoftInput();
                toLogin(getLoinBean(areaCode.concat(loginName), loginPassword));
            }
        } else {
            showToast(getString(R.string.Toast_F0));
        }
    }

    private LoginBean getLoinBean(String loginName, String password) {
        LoginBean bean = new LoginBean();
        bean.setLoginName(loginName);
        bean.setLoginPassword(password);
        bean.setLoginType(Constants.LP_KEY);
        return bean;
    }

    private void toLogin(LoginBean bean) {
        loadingDialog = showLoading();
        String loginName;
        if (bean != null) {
            loginName = bean.getLoginName();
        } else {
            loginName = areaCode.concat(edt_loginName.getText().toString());
        }
        AppMain.getInstance().goToLogin(this, bean, new UserSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                handleLoginResponse(code, errorMsg, jsonObject, loginName);
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

    private void switchAppLanguage() {
        Intent intent = new Intent(this, LanguageActivity.class);
        intent.putExtra(Constants.LOCAL_SETTING_LANGUAGE_FROM_ACTIVITY, Constants.LOCAL_LANGUAGE_FROM_LOGIN);
        startActivityForResult(intent, REQUEST_SET_LANGUAGE);
    }

    /**
     * 跳到App首页
     */
    private void gotoMainActivity() {
        ActivityManager.getInstance().openMainActivity(this);
    }

    @Override
    public void onBackPressed() {
        close();
    }

    private void close() {
        closeSoftInput();
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                close();
                return true;
            default:
                return false;
        }
    }

    public void closeSoftInput(EditText editText) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void closeSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    protected void closeLoading(LoadingDialog dialog) {
        LoadingDialog.dismissDialog(dialog);
    }

    protected LoadingDialog showLoading() {
        return LoadingDialog.showDialogLoading(this, getString(R.string.DialogTitle_C0));
    }

    protected LoadingDialog showLoading(String loading) {
        return LoadingDialog.showDialogLoading(this, loading);
    }

    public void showToast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            ToastUtil.showToastShort(getApplicationContext(), msg);
        }
    }

    protected boolean isCanClick() {
        return ToolUtils.isCanClick();
    }


    private int clickCount = 0;//记录连续点击次数

    private void testAccount() {
        if (ConfigHelper.DEBUG) {
            iv_logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickCount++;
                    tv_area_code.setText(ConfigCountry.KEY_ADD_AREA_CODE);
                    tv_area_code_otp.setText(ConfigCountry.KEY_ADD_AREA_CODE);
                    areaCode = ConfigCountry.KEY_AREA_CODE;
                    refreshForgotPwdUI();
                    if (clickCount == 1) {
                        edt_loginName_otp.setText("8123456781");
                        edt_loginName.setText("8123456781");
                        edt_login_pwd.setText("123456abc");
                    } else if (clickCount == 2) {
                        edt_loginName_otp.setText("813240357775");
                        edt_loginName.setText("813240357775");
                        edt_login_pwd.setText("123456a");
                    } else if (clickCount == 3) {
                        edt_loginName_otp.setText("815949855345");
                        edt_loginName.setText("815949855345");
                        edt_login_pwd.setText("369369m");
                    } else if (clickCount == 4) {
                        edt_loginName_otp.setText("813260108656");
                        edt_loginName.setText("813260108656");
                        edt_login_pwd.setText("123456a");
                    } else if (clickCount == 5) {
                        edt_loginName_otp.setText("8123456782");
                        edt_loginName.setText("8123456782");
                        edt_login_pwd.setText("123456");
                    } else if (clickCount == 6) {
                        edt_loginName_otp.setText("85921140561"); //豆豆帐号
                        edt_loginName.setText("85921140561"); //豆豆帐号
                        edt_login_pwd.setText("abc123456");
                    } else if (clickCount == 7) {
                        edt_loginName_otp.setText("85921141111"); //项硕帐号
                        edt_loginName.setText("85921141111"); //项硕帐号
                        edt_login_pwd.setText("a111111");
                    } else if (clickCount == 8) {
                        edt_loginName_otp.setText("8663663666666"); //新注册号码, 支付密码不设置, 实名不认证
                        edt_loginName.setText("8663663666666"); //新注册号码, 支付密码不设置, 实名不认证
                        edt_login_pwd.setText("369369l");
                    } else if (clickCount == 9) {
                        edt_loginName_otp.setText("815801006286"); //新注册号码, 支付密码不设置, 实名不认证
                        edt_loginName.setText("815801006286"); //新注册号码, 支付密码不设置, 实名不认证
                        edt_login_pwd.setText("123456a");
                        clickCount = 0;
                    }
                }
            });
        }
    }

    private void getUserAgreement() {
        loadingDialog = showLoading();
        AppMain.getInstance().getUserAgreement(this, Constants.REG_KEY, new OtherSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                loadingDialog.dismiss();
                UserAgreementRsp resp = UserAgreementRsp.decodeJson(UserAgreementRsp.class, jsonObject);
                if (resp != null && TextUtils.equals(code, OtherSdk.LOCAL_SUCCESS)) {
                    openSignUp(resp.version, resp.getAgreementSchemas());
                } else {
                    showToast(errorMsg);
                }
            }
        });
    }


    //用于控制 btn 、 edittext右侧删除按钮 显隐
    public void updateViewState(EditText editLogin, EditText editPwd) {

        String loginName = editLogin.getText().toString();
        String password = editPwd.getText().toString();

        if (TextUtils.isEmpty(loginName) || TextUtils.isEmpty(password)) {
            btn_login.setEnabled(false);
            return; //到这里就可以 return ；
        }

        if (AppGlobalUtil.getInstance().checkPhoneNum(areaCode, loginName)
                && password.length() >= PWD_MIN_LENGTH) {
            btn_login.setEnabled(true);

        } else {
            btn_login.setEnabled(false);
        }
    }

    //用于控制 btn 、 edittext右侧删除按钮 显隐
    public void updateViewStateOTP(EditText editLogin) {
        String loginName = editLogin.getText().toString();
        if (AppGlobalUtil.getInstance().checkPhoneNum(areaCode, loginName)) {
            btn_login_otp.setEnabled(true);

        } else {
            btn_login_otp.setEnabled(false);
        }
    }

    /**
     * 账号被锁定的弹窗
     */
    private class DialogBroadRec extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            if (TextUtils.equals(intent.getAction(), Constants.ACTION_ACCOUNT_OUT)) {
                showOutDialog(intent.getStringExtra(Constants.MSG_KEY));
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }
        }
    }

    /**
     * 显示被踢出的dialog
     *
     * @param msg
     */
    private void showOutDialog(String msg) {
        LogUtil.d(TAG, msg);
        dialog = new CommonDialog
                .Builder(this)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(getString(com.ahdi.lib.utils.R.string.DialogButton_B0), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CleanConfigUtil.cleanAllConfig();
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SET_LANGUAGE && resultCode == RESULT_OK) {
            String currentLanguage = LanguageUtil.getLanguage(this);
            if (!TextUtils.isEmpty(currentLanguage) && !TextUtils.equals(lastLanguage, currentLanguage)) {
                try {
                    ViewTreeObserver viewTreeObserver = edt_login_pwd.getViewTreeObserver();
                    removeViewTreeObserver(viewTreeObserver, globalLayoutListener);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                recreate();
            }
        } else if (requestCode == REQUEST_OPEN_FORGOT && resultCode == RESULT_CLOSE_FORGOT) {
            clearEdtFocus();

        } else if (requestCode == GuideTouchIDUnlockActivity.OPEN_REQUEST_CODE
                && resultCode == GuideTouchIDUnlockActivity.CLOSE_RESULT_CODE) {
            isUnlockSkip = true;
            openMainActivity(GuideTouchIDUnlockActivity.OPEN_REQUEST_CODE);
        }
    }

    private void openMainActivity(int openRequestCode) {
        Intent intent = new Intent(this, AppMainActivity.class);
        intent.putExtra(Constants.LOCAL_FROM_KEY, GuideTouchIDUnlockActivity.TAG);
        startActivityForResult(intent, openRequestCode);
    }

    private void clearEdtFocus() {
        closeSoftInput(edt_loginName_otp);
        closeSoftInput(edt_loginName);
        closeSoftInput(edt_login_pwd);
        edt_loginName_otp.clearFocus();
        edt_loginName.clearFocus();
        edt_login_pwd.clearFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }


    @Override
    public void doAnything(int request_code) {
        LBSInstance.getInstance().getLBS(new LBSInstance.LBSCallBack() {
            @Override
            public void onSuccess(BDLocation location) {
                LogUtil.d(TAG, "getStreet  ===: " + location.getStreet()  );
                LogUtil.d(TAG, "lat  ===: " + location.getLatitude() );
                LogUtil.d(TAG, "lng  ===: " + location.getLongitude() );

                if(!TextUtils.isEmpty(location.getStreet() )){
                    AppGlobalUtil.getInstance().putString(getApplicationContext(), ConfigSP.SP_KEY_STREET_NAME, location.getStreet() );
                }

            }

            @Override
            public void onFail(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "errorCode  ===: " + errorCode );
                LogUtil.d(TAG, "errorMsg  ===: " + errorMsg );
            }
        });
    }
}

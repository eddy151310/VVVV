package com.ahdi.wallet.app.ui.aaaa.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.base.BaseEditTextWatcher;
import com.ahdi.lib.utils.base.GlobalContextWrapper;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.config.ConfigSP;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.CountdownUtil;
import com.ahdi.lib.utils.utils.LBSInstance;
import com.ahdi.lib.utils.utils.LanguageUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ScreenAdaptionUtil;
import com.ahdi.lib.utils.utils.StatusBarUtil;
import com.ahdi.lib.utils.widgets.DeleteEditText;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.HttpReqApp;
import com.ahdi.wallet.app.request.aaa.LoginSMSReq;
import com.ahdi.wallet.app.request.aaa.SmsCodeReq;
import com.ahdi.wallet.app.response.aaa.LoginSMSRsp;
import com.ahdi.wallet.app.response.aaa.SmsCodeRsp;
import com.ahdi.wallet.app.ui.adapters.listener.PhoneTextWatcher;
import com.baidu.location.BDLocation;

import org.json.JSONObject;

public class LoginActivity2 extends AppBaseActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity2.class.getSimpleName();

    private EditText phoneEdit;
    private DeleteEditText codeEdit;
    private TextView tv_login_number_error_tip, tv_send_code, btn_submit, tv_agreement;
    private LinearLayout ll_agreement;
    private ScrollView scrollView;

    private ViewTreeObserver.OnGlobalLayoutListener listener;
    private CountdownUtil countdownUtil;
    private LoadingDialog loadingDialog = null;
    /**是否正在倒计时*/
    private boolean isCountdowning = false;

    SmsCodeRsp smsCodeRsp ;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenAdaptionUtil.setCustomDensity(this, getApplication());
        setContentView(R.layout.aaaa_activity_login);
        getWindow().setBackgroundDrawableResource(R.color.CC_F1F2F6); //代码设置bg 防止键盘弹起 闪黑屏
        StatusBarUtil.setStatusBar(this);
        initView();
        initData();
        initSoftKeyboard();

        if(!checkPermissions(request_type_lbs)){
            requestPermissions(request_type_lbs);
        }else{
            doAnything(request_type_lbs);
        }
    }



    private void initView() {

        ((TextView)findViewById(R.id.tv_area_code)).setText("+86");
        phoneEdit = findViewById(R.id.et_login_phone_number);
        tv_login_number_error_tip = findViewById(R.id.tv_login_number_error_tip);

        codeEdit = findViewById(R.id.et_verify_code);
        tv_send_code = findViewById(R.id.tv_send_code);
        tv_send_code.setOnClickListener(this);

        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);

        scrollView = findViewById(R.id.scrollView);

        tv_agreement = findViewById(R.id.tv_agreement);
        tv_agreement.setHighlightColor(getResources().getColor(android.R.color.transparent));
        createLink(this , tv_agreement , "《XXXX用户协议》");

        ll_agreement = findViewById(R.id.ll_agreement);

        phoneEdit.addTextChangedListener(new PhoneTextWatcher(phoneEdit){

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //父类根据输入内容处理输入框的长度
                super.onTextChanged(s, start, before, count);
                updateViewState();
            }
        });
        codeEdit.addTextChangedListener(new BaseEditTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateViewState();
            }
        });

        codeEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !verifyAccount()){
                    tv_login_number_error_tip.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initData() {
        String loginName = "15601234442";
        String smsCode = "666666";
        phoneEdit.setText(loginName);
        phoneEdit.setSelection(loginName.length());
        codeEdit.setText(smsCode);
        codeEdit.setSelection(smsCode.length());
    }

    /**
     * 用于控制 btn、错误提示的显示和隐藏
     */
    public void updateViewState() {
        if (verifyAccount()){
            tv_login_number_error_tip.setVisibility(View.GONE);
            //账号输入符合规则之后如果正在倒计时不可以把发送按钮设置为可用
            if (!isCountdowning){
                tv_send_code.setEnabled(true);
            }
            if (verifyCode()){
                btn_submit.setEnabled(true);
            }else {
                btn_submit.setEnabled(false);
            }
        }else {
            tv_send_code.setEnabled(false);
            btn_submit.setEnabled(false);
        }
    }

    /**
     * 判断账号输入框是否符合规则
     * @return
     */
    private boolean verifyAccount(){
        String loginName = phoneEdit.getText().toString();
        if (TextUtils.isEmpty(loginName)){
            return false;
        }

        if (loginName.length() >= ConfigCountry.PHONE_LIMIT_MIN_LENGTH_8 && loginName.length() <= ConfigCountry.PHONE_LIMIT_MAX_LENGTH_13  ) {
            return true;
        }

        return  false;
    }



    /**
     * 判断验证码是否合法
     * @return
     */
    private boolean verifyCode(){
        String code = codeEdit.getText().toString();
        if (!TextUtils.isEmpty(code)){
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        if (id == R.id.tv_send_code){
            getSMSCode();
        }else if (id == R.id.btn_submit) {
            closeSoftInput();
            login(codeEdit.getText().toString().trim());
        }
    }

    /**
     * 登录(SMS)
     */
    private void login(@NonNull String smsCode) {
        loadingDialog = showLoading();
        LogUtil.d(TAG, "执行登录");
        if(smsCodeRsp != null && !TextUtils.isEmpty(smsCodeRsp.orderId)){
            LoginSMSReq request = new LoginSMSReq(phoneEdit.getText().toString().trim() ,smsCode , smsCodeRsp.orderId);
            HttpReqApp.getInstance().onLoginSMS(request, new HttpReqTaskListener() {

                @Override
                public void onPostExecute(@NonNull JSONObject json) {
                    loadingDialog.dismiss();
                    LogUtil.d(TAG,   "解析json :" + json);
                    ToastUtil.showToastAtCenterLong(LoginActivity2.this , smsCodeRsp.getmHeader().retCode + smsCodeRsp.getmHeader().retMsg );
                    LoginSMSRsp responseLogin = LoginSMSRsp.decodeJson(LoginSMSRsp.class ,json);
                    if(responseLogin.getmHeader().retCode.equals(Constants.RET_CODE_SUCCESS)){
                        GlobalApplication.getApplication().setSID(responseLogin.sid);
                        GlobalApplication.getApplication().setUserID(responseLogin.userId);
                    }

                }

                @Override
                public void onError(@NonNull JSONObject json) {
                    LogUtil.d(TAG,   "解析json onError :" + json);
                    loadingDialog.dismiss();
                }
            });
        }else{
            ToastUtil.showToastAtCenterLong(LoginActivity2.this ,"SMS orderID is null");
        }

    }


    /**
     * 获取短信验证码
     */
    private void getSMSCode() {
        smsCodeRsp = null;
        loadingDialog = showLoading();
        LogUtil.d(TAG, "点击发送验证码按钮");
        //13001163475
        SmsCodeReq request = new SmsCodeReq(phoneEdit.getText().toString().trim());
        HttpReqApp.getInstance().onSMSCode(request, new HttpReqTaskListener() {

            @Override
            public void onPostExecute(JSONObject json) {
                loadingDialog.dismiss();
                LogUtil.d(TAG,   "解析json :" + json);
                smsCodeRsp = SmsCodeRsp.decodeJson(SmsCodeRsp.class, json);
                ToastUtil.showToastAtCenterLong(LoginActivity2.this , smsCodeRsp.getmHeader().retCode + smsCodeRsp.getmHeader().retMsg );
            }

            @Override
            public void onError(JSONObject json) {
                LogUtil.d(TAG,   "解析json onError :" + json);
                loadingDialog.dismiss();
            }
        });
        detailOTP(false);
    }

    /**
     * 处理倒计时
     * @param isEnabled
     */
    private void detailOTP(boolean isEnabled){
        if(isEnabled){
            tv_send_code.setText("锁定");
            tv_send_code.setEnabled(false);
        }else {
            startCount(20);
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
                isCountdowning = true;
                tv_send_code.setEnabled(false);
                tv_send_code.setText(count + "s");
            }

            @Override
            public void onComplete() {
                isCountdowning = false;
                tv_send_code.setText("验证码");
                if (verifyAccount()){
                    tv_send_code.setEnabled(true);
                }else {
                    tv_send_code.setEnabled(false);
                }
            }
        });
    }

    private void stopCount() {
        if (countdownUtil != null) {
            countdownUtil.destroyTimer();
        }
    }



    /**
     * 初始化布局变化监听
     */
    private void initSoftKeyboard() {
        ViewTreeObserver viewTreeObserver = phoneEdit.getViewTreeObserver();
        removeViewTreeObserver(viewTreeObserver, listener);
        listener = new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                phoneEdit.getWindowVisibleDisplayFrame(r);
                int screenHeight = phoneEdit.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom);

                if (heightDifference > 200) {
                    ll_agreement.setVisibility(View.GONE);
                } else {
                    ll_agreement.setVisibility(View.VISIBLE);
                }
            }
        };
        viewTreeObserver.addOnGlobalLayoutListener(listener);
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

    @Override
    public void closeSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        smsCodeRsp = null;
    }


    @Override
    public void doAnything(int requestCode) {
        LBSInstance.getInstance().getLBS(new LBSInstance.LBSCallBack() {
            @Override
            public void onSuccess(BDLocation location) {
                LogUtil.d(TAG, "getStreet  ===: " + location.getStreet() );
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


    public void createLink(Activity mActivity, TextView tv , String link) {
        SpannableString spannableString = new SpannableString(link);
        spannableString.setSpan(new CustomUrlSpan(mActivity ,"http://www.baidu.com"), 0, link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spannableString);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }


    public class CustomUrlSpan extends ClickableSpan {
        private Context context;
        private String url;

        public CustomUrlSpan(Context context, String url) {
            this.context = context;
            this.url = url;
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            /**set textColor**/
            ds.setColor(ds.linkColor);
            /**Remove the underline**/
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            openWebCommonActivity("http://www.baidu.com");
        }
    }

}

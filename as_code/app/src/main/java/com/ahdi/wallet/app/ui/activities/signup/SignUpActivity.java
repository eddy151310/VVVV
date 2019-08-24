package com.ahdi.wallet.app.ui.activities.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.wallet.app.response.QueryRegisterRsp;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.widgets.CheckCodeDialogUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.ui.adapters.listener.PhoneTextWatcher;
import com.ahdi.wallet.app.ui.base.BaseClickableSpan;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author admin
 */
public class SignUpActivity extends AppBaseActivity {

    //手机号码输入区域
    private EditText et_sign_up_phone;
    private View ll_bottom;
    /**
     * 确定按钮
     */
    private Button btn_sure;

    private CheckBox cb_agree;

    private SpannableStringBuilder agreementText = new SpannableStringBuilder();
    private String agreeVer;

    private String areaCode = ConfigCountry.KEY_AREA_CODE;
    private PhoneTextWatcher phoneTextWatcher;
    private ScrollView master_layout;
    private ViewTreeObserver.OnGlobalLayoutListener listener;
    private int masterBottom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initAgreementData();
        setContentView(R.layout.layout_activity_sign_up);
        getWindow().setBackgroundDrawableResource(R.color.CC_F1F2F6); //代码设置bg 防止键盘弹起 闪黑屏
        initCommonTitle(getString(R.string.SignUpPhone_A0));
        initView();
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_area_code)).setText(ConfigCountry.KEY_ADD_AREA_CODE);
        master_layout = findViewById(R.id.master_layout);
        master_layout.getParent().requestDisallowInterceptTouchEvent(true);
        getMasterLayoutBottom();
        //输入手机号码
        et_sign_up_phone = findViewById(R.id.et_sign_up_phone);

        cb_agree = findViewById(R.id.cb_agree);
        cb_agree.setChecked(false); // 默认未选中

        TextView tv_agreement = findViewById(R.id.tv_agreement);
        tv_agreement.setHighlightColor(getResources().getColor(android.R.color.transparent));
        tv_agreement.setMovementMethod(LinkMovementMethod.getInstance());
        tv_agreement.setText(agreementText);

        btn_sure = findViewById(R.id.btn_sure);
        btn_sure.setEnabled(false);
        btn_sure.setOnClickListener(this);

        ll_bottom = findViewById(R.id.ll_bottom);

        cb_agree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkPhoneLength(et_sign_up_phone.getText().toString());
                } else {
                    btn_sure.setEnabled(false);
                }
            }
        });
        onMatchPhoneNum();
        initSoftKeyboard();
    }

    private void getMasterLayoutBottom() {
        ViewTreeObserver viewTreeObserver = master_layout.getViewTreeObserver();
        removeViewTreeObserver(viewTreeObserver, listener);
        listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                masterBottom = master_layout.getBottom();
            }
        };
        viewTreeObserver.addOnGlobalLayoutListener(listener);
    }

    private void initSoftKeyboard() {
        android.view.ViewTreeObserver viewTreeObserver = et_sign_up_phone.getViewTreeObserver();
        if (viewTreeObserver != null) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    removeViewTreeObserver(viewTreeObserver, this);
                    android.graphics.Rect r = new android.graphics.Rect();
                    et_sign_up_phone.getWindowVisibleDisplayFrame(r);
                    int screenHeight = et_sign_up_phone.getRootView().getHeight();
                    int heightDifference = screenHeight - (r.bottom);
                    if (heightDifference > 200) {
                        ll_bottom.setVisibility(View.GONE);
                        //软键盘显示
                        master_layout.scrollTo(0, masterBottom);
                    } else {
                        ll_bottom.setVisibility(View.VISIBLE);
                        //软键盘隐藏
                        master_layout.scrollTo(0, 0);
                    }
                }
            });
        }
    }

    private void checkPhoneLength(String phone) {
        if (AppGlobalUtil.getInstance().checkPhoneNum(areaCode, phone)
                && cb_agree.isChecked()) {
            btn_sure.setEnabled(true);

        } else {
            btn_sure.setEnabled(false);
        }
    }

    private void onMatchPhoneNum() {
        phoneTextWatcher = new PhoneTextWatcher(et_sign_up_phone, areaCode) {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //父类根据输入内容处理输入框的长度
                super.onTextChanged(s, start, before, count);
                String phone = et_sign_up_phone.getText().toString();
                checkPhoneLength(phone);
            }
        };
        et_sign_up_phone.addTextChangedListener(phoneTextWatcher);
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.btn_sure) {
            closeSoftInput();
            checkPhoneNum();
        }
    }

    private void checkPhoneNum() {
        String phoneNum = et_sign_up_phone.getText().toString();
        if (TextUtils.equals(areaCode, ConfigCountry.KEY_AREA_CODE)) {
            if (phoneNum.startsWith(ConfigCountry.PHONE_PREFIX_8)
                    || phoneNum.startsWith(ConfigCountry.PHONE_PREFIX_08)) {
                queryRegister(areaCode.concat(phoneNum));
            } else {
                showToast(getString(R.string.DialogMsg_X0));
            }
        } else {
            queryRegister(areaCode.concat(phoneNum));
        }
    }

    /**
     * 查询是否已经注册
     *
     * @param phoneNum
     */
    private void queryRegister(String phoneNum) {
        LoadingDialog loadingDialog = showLoading();
        AppMain.getInstance().queryRegister(SignUpActivity.this, phoneNum, 0, new UserSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                QueryRegisterRsp resp = QueryRegisterRsp.decodeJson(QueryRegisterRsp.class, jsonObject);
                if (resp != null && TextUtils.equals(code, UserSdk.LOCAL_PAY_SUCCESS)) {
                    if (TextUtils.equals(areaCode, ConfigCountry.KEY_AREA_CODE)) {
                        if (resp.SMSSwitch == 1) {
                            //打开短信验证码界面
                            openVerifyCode(phoneNum, resp.SLName);
                        } else if (resp.SMSSwitch == 0) {
                            //跳过短信验证码界面, 直接打开设置密码
                            openSetLoginPwd(phoneNum, resp.SLName);
                        }
                    } else {
                        //跳过短信验证码界面, 直接打开设置密码
                        openSetLoginPwd(phoneNum, resp.SLName);
                    }
                } else if (TextUtils.equals(code, Constants.RET_CODE_PP010)) {
                    CheckCodeDialogUtil.getInstance().showDialog(SignUpActivity.this, errorMsg, Constants.RET_CODE_SUCCESS);
                } else {
                    showToast(errorMsg);
                }
            }
        });
    }

    private void openSetLoginPwd(String phoneNum, String slname) {
        openNext(phoneNum, slname, true);
    }

    private void openVerifyCode(String phoneNum, String slname) {
        openNext(phoneNum, slname, false);
    }

    /**
     * @param phoneNum
     * @param slname
     * @param isSkipVerify false: 打开验证码界面  true: 跳过验证码界面, 打开设置密码界面
     */
    private void openNext(String phoneNum, String slname, boolean isSkipVerify) {
        Intent intent;
        if (!isSkipVerify) {
            intent = new Intent(this, SigupVerifyCodeActivity.class);
        } else {
            intent = new Intent(this, SetLoginPwdActivity.class);
        }
        intent.putExtra(Constants.PH_KEY, phoneNum);
        intent.putExtra(Constants.LOCAL_SLNAME_KEY, slname);
        intent.putExtra(Constants.LOCAL_VERSION_KEY, agreeVer);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initAgreementData() {
        Intent intent = getIntent();
        HashMap<String, String> agreeMap = null;
        if (intent != null) {
            agreeVer = intent.getStringExtra(Constants.LOCAL_VERSION_KEY);
            agreeMap = (HashMap<String, String>) intent.getSerializableExtra(Constants.LOCAL_AGREEMENTS_KEY);
        }
        if (agreeMap == null) {
            return;
        }
        SpannableString prefix = new SpannableString(getString(R.string.SignUpPhone_F0));
        ForegroundColorSpan color = new ForegroundColorSpan(getResources().getColor(R.color.CC_282934));
        prefix.setSpan(color, 0, prefix.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        agreementText.append(prefix);

        for (Iterator i = agreeMap.keySet().iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            SpannableString text = new SpannableString(key.concat(getString(R.string.SignUpPhone_G0)));
            String url = agreeMap.get(key);
            BaseClickableSpan baseClickableSpan = new BaseClickableSpan(this,
                    new BaseClickableSpan.OnClickableSpaListener() {
                        @Override
                        public void onClick(View widget) {
                            openWebCommonActivity(url);
                        }
                    });
            baseClickableSpan.setTextColor(getResources().getColor(R.color.CC_D63031));
            baseClickableSpan.setUnderlineText(false);
            text.setSpan(baseClickableSpan, 0, key.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            agreementText.append(text);
            if (!i.hasNext()) {
                SpannableString textAnd = new SpannableString(getString(R.string.SignUpPhone_G0));
                ForegroundColorSpan colorAnd = new ForegroundColorSpan(getResources().getColor(R.color.CC_282934));
                textAnd.setSpan(colorAnd, 0, textAnd.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                agreementText.append(textAnd);
            }
        }
    }
}

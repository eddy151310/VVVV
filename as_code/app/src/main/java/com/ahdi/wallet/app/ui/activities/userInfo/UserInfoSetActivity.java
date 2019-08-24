package com.ahdi.wallet.app.ui.activities.userInfo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.bean.UserInfoGuideSetBean;
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.wallet.app.response.UserInfoGuideSetRsp;
import com.ahdi.wallet.app.schemas.UserSchema;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.fingerprint.FingerprintSDK;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.DateUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.StringUtil;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.datepicker.DatePickerView;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.lib.utils.widgets.dialog.ListSelectDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.lib.utils.base.BaseEditTextWatcher;
import com.ahdi.lib.utils.base.BaseTextViewWatcher;
import com.ahdi.wallet.app.ui.activities.biometric.GuideTouchIDUnlockActivity;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Date: 2018/6/26 下午2:06
 * Author: kay lau
 * Description: 注册完成, 引导设置用户信息
 */
public class UserInfoSetActivity extends AppBaseActivity {

    private static final String TAG = "UserInfoSetActivity";
    private static final int NICK_NAME_MAX_LENGTH = 32;
    private static final int EMAIL_MAX_LENGTH = 32;
    private static final int EMAIL_MIN_LENGTH = 6;
    private static final String startDate = "1900-01-01 00:01";
    private static final String defaultDate = "1990-06-15 00:01";

    private View rl_user_gender_area, rl_user_birthday_area;
    private EditText et_nick_name, et_user_email;
    private TextView tv_user_gender, tv_user_birthday;
    private Button btn_confirm;
    private DatePickerView datePickerView;
    private DateResultHandler resultHandler = null;
    private SubmitHandler mHandler;
    private int defaultSelect = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_user_info_set);
        initCommonTitle(getString(R.string.UserInfoGuideSet_A0));
        initTitleBack().setVisibility(View.INVISIBLE);
        initView();
        initData();
    }

    private void initView() {

        et_nick_name = findViewById(R.id.et_nick_name);
        et_nick_name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(NICK_NAME_MAX_LENGTH)});

        et_user_email = findViewById(R.id.et_user_email);
        et_user_email.setFilters(new InputFilter[]{new InputFilter.LengthFilter(EMAIL_MAX_LENGTH)});

        rl_user_gender_area = findViewById(R.id.rl_user_gender_area);
        rl_user_birthday_area = findViewById(R.id.rl_user_birthday_area);

        tv_user_gender = findViewById(R.id.tv_user_gender);
        tv_user_birthday = findViewById(R.id.tv_user_birthday);

        btn_confirm = findViewById(R.id.btn_confirm);

        initTextChanged();
    }

    private void initData() {
        rl_user_gender_area.setOnClickListener(this);
        rl_user_birthday_area.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
    }

    private void initTextChanged() {

        et_nick_name.addTextChangedListener(new BaseEditTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String gender = tv_user_gender.getText().toString();
                String birthday = tv_user_birthday.getText().toString();
                String nickName = et_nick_name.getText().toString();
                String email = et_user_email.getText().toString();

                if (!TextUtils.isEmpty(nickName)) {

                    if (!TextUtils.isEmpty(email) && email.length() >= EMAIL_MIN_LENGTH
                            && !TextUtils.isEmpty(birthday) && !TextUtils.isEmpty(gender)) {

                        btn_confirm.setEnabled(true);

                    } else {
                        btn_confirm.setEnabled(false);
                    }
                } else {
                    btn_confirm.setEnabled(false);
                }
            }
        });

        et_user_email.addTextChangedListener(new BaseEditTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String gender = tv_user_gender.getText().toString();
                String birthday = tv_user_birthday.getText().toString();
                String nickName = et_nick_name.getText().toString();
                String email = et_user_email.getText().toString();

                if (!TextUtils.isEmpty(email)) {

                    if (!TextUtils.isEmpty(nickName) && email.length() >= EMAIL_MIN_LENGTH
                            && !TextUtils.isEmpty(birthday) && !TextUtils.isEmpty(gender)) {

                        btn_confirm.setEnabled(true);

                    } else {
                        btn_confirm.setEnabled(false);
                    }
                } else {
                    btn_confirm.setEnabled(false);
                }
            }
        });

        tv_user_gender.addTextChangedListener(new BaseTextViewWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                String gender = tv_user_gender.getText().toString();
                String birthday = tv_user_birthday.getText().toString();
                String nickName = et_nick_name.getText().toString();
                String email = et_user_email.getText().toString();

                if (!TextUtils.isEmpty(gender)) {
                    if (!TextUtils.isEmpty(birthday) && !TextUtils.isEmpty(nickName)
                            && !TextUtils.isEmpty(email) && email.length() >= EMAIL_MIN_LENGTH) {

                        btn_confirm.setEnabled(true);

                    } else {
                        btn_confirm.setEnabled(false);
                    }
                } else {
                    btn_confirm.setEnabled(false);
                }
            }
        });

        tv_user_birthday.addTextChangedListener(new BaseTextViewWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                String gender = tv_user_gender.getText().toString();
                String birthday = tv_user_birthday.getText().toString();
                String nickName = et_nick_name.getText().toString();
                String email = et_user_email.getText().toString();

                if (!TextUtils.isEmpty(birthday)) {
                    if (!TextUtils.isEmpty(gender) && !TextUtils.isEmpty(nickName)
                            && !TextUtils.isEmpty(email) && email.length() >= EMAIL_MIN_LENGTH) {

                        btn_confirm.setEnabled(true);

                    } else {
                        btn_confirm.setEnabled(false);
                    }
                } else {
                    btn_confirm.setEnabled(false);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (!isCanClick()) {
            return;
        }
        if (id == R.id.rl_user_gender_area) {
            // 选择性别
            selectGender();

        } else if (id == R.id.rl_user_birthday_area) {
            // 设置生日
            setUserBirthday();

        } else if (id == R.id.btn_confirm) {
            // 校验邮箱格式 输入框长度最大24
            if (!et_user_email.getText().toString().matches(Constants.EMAIL_REGEX)) {
                showErrorDialog(getString(R.string.DialogMsg_G0));
                return;
            }
            String nname = et_nick_name.getText().toString().trim();
            if (TextUtils.isEmpty(nname)) {
                et_nick_name.setText("");
                ToastUtil.showToastShort(UserInfoSetActivity.this, getString(R.string.Toast_T0));
                return;
            }
            // 提交信息
            if (mHandler == null) {
                mHandler = new SubmitHandler();
            }
            Message message = mHandler.obtainMessage(0);
            mHandler.sendMessageDelayed(message, 200);

        }
    }

    private void selectGender() {
        ArrayList<String> list = StringUtil.getGenderList(this);
        new ListSelectDialog(this, R.style.ActionSheetDialogStyle, list, defaultSelect)
                .setOnGenderDialogListener(new ListSelectDialog.OnListSelectListener() {
                    @Override
                    public void onCallBack(int selectedIndex, String item) {
                        LogUtil.e(TAG, "selectedIndex: " + selectedIndex);
                        defaultSelect = selectedIndex;
                        tv_user_gender.setText(list.get(selectedIndex));
                    }
                }).show();
    }

    private void setUserBirthday() {
        if (resultHandler == null) {
            resultHandler = new DateResultHandler();
        }
        String endDate = DateUtil.formatTimeYMDHM(System.currentTimeMillis(), null);
        datePickerView = new DatePickerView(this, resultHandler, startDate, endDate);
        datePickerView.showDay(true);
        datePickerView.show(defaultDate);
    }

    /**
     * 提交用户信息引导设置
     */
    private void onConfirm() {
        LoadingDialog loadingDialog = showLoading();
        String nname = et_nick_name.getText().toString().trim();
        String email = et_user_email.getText().toString();
        String gender = tv_user_gender.getText().toString();
        String birthday = tv_user_birthday.getText().toString();
        UserInfoGuideSetBean guideSetBean = new UserInfoGuideSetBean(nname, birthday, gender, email);

        AppMain.getInstance().userInfoGuideSet(this, guideSetBean, new UserSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                closeLoading(loadingDialog);
                UserInfoGuideSetRsp response = UserInfoGuideSetRsp.decodeJson(UserInfoGuideSetRsp.class, jsonObject);
                if (TextUtils.equals(code, UserSdk.LOCAL_PAY_SUCCESS) && response != null) {
                    UserSchema userSchema = response.getUser();
                    GlobalApplication.getApplication().updateUserSchema(userSchema);

                    String LName = AppGlobalUtil.getInstance().getString(UserInfoSetActivity.this, Constants.LOCAL_LNAME_KEY);
                    if (!TouchIDStateUtil.isSkipGuideUnlock(UserInfoSetActivity.this, LName)
                            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && FingerprintSDK.isSupport(UserInfoSetActivity.this)
                            && FingerprintSDK.isHasFingerprints(UserInfoSetActivity.this)
                            && !TouchIDStateUtil.isStartTouchIDUnlock(UserInfoSetActivity.this, LName + Constants.START_TOUCH_ID_LOGIN)) {
                        // 引导设置指纹解锁登录
                        Intent intent = new Intent(UserInfoSetActivity.this, GuideTouchIDUnlockActivity.class);
                        startActivityForResult(intent, GuideTouchIDUnlockActivity.OPEN_REQUEST_CODE);

                    } else {
                        ActivityManager.getInstance().openMainActivity(UserInfoSetActivity.this);
                    }
                } else if (TextUtils.equals(code, Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION)) {
                    showToast(errorMsg);

                } else {
                    showErrorDialog(errorMsg);
                }
            }
        });
    }

    class DateResultHandler implements DatePickerView.ResultHandler {
        @Override
        public void handle(long time) {
            LogUtil.d(TAG, DateUtil.formatTimeYMDHM(time, null));
            tv_user_birthday.setText(DateUtil.formatTimeDMYNoHMS(time));
        }
    }

    @SuppressLint("HandlerLeak")
    class SubmitHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                onConfirm();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (datePickerView != null && datePickerView.isShowing()) {
            datePickerView.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GuideTouchIDUnlockActivity.OPEN_REQUEST_CODE
                && resultCode == GuideTouchIDUnlockActivity.CLOSE_RESULT_CODE) {
            ActivityManager.getInstance().openMainActivity(this);
            finish();
        }
    }
}

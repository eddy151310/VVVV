package com.ahdi.wallet.app.ui.activities.other;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ahdi.wallet.app.sdk.AccountSdk;
import com.ahdi.wallet.app.sdk.OtherSdk;
import com.ahdi.wallet.app.callback.AccountSdkCallBack;
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.wallet.app.response.CheckPayPwdRsp;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.fingerprint.FingerprintSDK;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.CleanConfigUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.callback.AppConfigCallBack;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.ui.activities.biometric.TouchIDActivity;
import com.ahdi.wallet.app.ui.activities.loginPwd.ResetLoginPwdActivity;
import com.ahdi.wallet.app.ui.activities.payPwd.PayPwdGuideSetActivity;
import com.ahdi.wallet.app.ui.activities.signup.SetLoginPwdActivity;
import com.ahdi.wallet.app.ui.widgets.SettingsDialog;
import com.ahdi.wallet.util.AppConfigHelper;

import org.json.JSONObject;

public class SettingsActivity extends AppBaseActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private final static int REQUEST_PARAM_INT_CODE = 1022;

    private TextView tv_set_pay_pwd;
    private TextView tv_set_login_pwd;
    private SettingsDialog dialog;

    private LoadingDialog loadingDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isRecreateActivity() && isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.layout_activity_settings);
        initCommonTitle(getString(R.string.Setting_A0));
        initView();
        initData();
    }

    public void initView() {
        tv_set_pay_pwd = findViewById(R.id.tv_set_pay_pwd);
        tv_set_login_pwd = findViewById(R.id.tv_set_login_pwd);
        findViewById(R.id.re_settings_reset_login_pwd).setOnClickListener(this);
        findViewById(R.id.re_settings_set_pay_pwd).setOnClickListener(this);
        findViewById(R.id.re_settings_about).setOnClickListener(this);
        findViewById(R.id.re_settings_help).setOnClickListener(this);
        findViewById(R.id.re_settings_logOut).setOnClickListener(this);

        findViewById(R.id.re_settings_language).setOnClickListener(this);

        View re_settings_touch_id = findViewById(R.id.re_settings_touch_id);
        View line = findViewById(R.id.line);
        // android 6.0以上 并且支持指纹时, 显示TouchID, 否则隐藏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && FingerprintSDK.isSupport(this)) {
            re_settings_touch_id.setVisibility(View.VISIBLE);
            re_settings_touch_id.setOnClickListener(this);
            line.setVisibility(View.VISIBLE);
        } else {
            re_settings_touch_id.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }
        refreshUI();
    }

    private void refreshUI() {
        String status = ProfileUserUtil.getInstance().getAccountStatus();
        boolean isHasPwd = ProfileUserUtil.getInstance().isHasPwd();
        if (!TextUtils.equals(status, Constants.STATUE_UNSAFE_KEY)) {
            tv_set_pay_pwd.setText(R.string.Setting_C0);
        } else {
            tv_set_pay_pwd.setText(R.string.Setting_D0);
        }
        if (isHasPwd) {
            tv_set_login_pwd.setText(getString(R.string.Setting_B0));
        } else {
            tv_set_login_pwd.setText(getString(R.string.Setting_M0));
        }
    }

    private void initData() {
        AppGlobalUtil.getInstance().putString(this, Constants.LOCAL_RECREATE_SETTING_ACTIVITY, "");
    }

    /**
     * 是不是重新创建页面
     *
     * @return
     */
    private boolean isRecreateActivity() {
        return !TextUtils.isEmpty(AppGlobalUtil.getInstance().getString(this, Constants.LOCAL_RECREATE_SETTING_ACTIVITY));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRecreateActivity()) {
            recreate();
        } else {
            refreshUI();
        }
    }

    @Override
    public void onClick(View view) {
        if (!isCanClick()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.re_settings_set_pay_pwd) {
            openSetPayPwd();    // 设置支付密码

        } else if (id == R.id.re_settings_reset_login_pwd) {
            boolean isHasPwd = ProfileUserUtil.getInstance().isHasPwd();
            if (isHasPwd) {
                openResetLoginPwd();// 重置登录密码
            } else {
                String lName = "";
                if (ProfileUserUtil.getInstance().getUserData() != null) {
                    lName = ProfileUserUtil.getInstance().getUserData().getsLName();
                }
                Intent intent = new Intent(this, SetLoginPwdActivity.class);
                intent.putExtra(Constants.LOCAL_FROM_KEY, Constants.LOCAL_FROM_SET_PWD);
                intent.putExtra(Constants.LOCAL_SLNAME_KEY, lName);
                startActivityForResult(intent, REQUEST_PARAM_INT_CODE);
            }

        } else if (id == R.id.re_settings_about) {
            openAbout();        // 打开关于页面

        } else if (id == R.id.re_settings_help) {
            openHelp();         // 打开帮助页面

        } else if (id == R.id.re_settings_logOut) {
            openLogoutDialog(); // 打开登出弹层

        } else if (id == R.id.re_settings_language) {
            switchAppLanguage();

        } else if (id == R.id.re_settings_touch_id) {
            openTouchID();
        }
    }

    private void switchAppLanguage() {
        Intent intent = new Intent(this, LanguageActivity.class);
        intent.putExtra(Constants.LOCAL_SETTING_LANGUAGE_FROM_ACTIVITY, Constants.LOCAL_LANGUAGE_FROM_SETTING);
        startActivity(intent);
    }

    private void openLogoutDialog() {
        dialog = new SettingsDialog(this, R.style.ActionSheetDialogStyle);
        dialog.show();//显示对话框
        dialog.setOnSettingsDialogListener(new SettingsDialog.OnSettingsDialogListener() {
            @Override
            public void onCallBack() {
                onLogout();
            }
        });
    }

    /**
     * 更新配置
     */
    private void openHelp() {
        loadingDialog = showLoading();
        AppMain.getInstance().appConfig(this, new AppConfigCallBack() {
            @Override
            public void onResult(String Code, String errorMsg) {
                closeLoading(loadingDialog);
                if (TextUtils.equals(Code, OtherSdk.LOCAL_SUCCESS)) {
                    openWebCommonActivity(AppConfigHelper.getInstance().getHelpUrl());
                } else {
                    showToast(errorMsg);
                }
            }
        });
    }

    private void openAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    private void openResetLoginPwd() {
        Intent intent = new Intent(this, ResetLoginPwdActivity.class);
        startActivity(intent);
    }

    private void openSetPayPwd() {
        String status = ProfileUserUtil.getInstance().getAccountStatus();
        if (!TextUtils.equals(status, Constants.STATUE_UNSAFE_KEY)) {
            onCheckPayPwd();
        } else {
            // 引导设置支付密码
            Intent intent = new Intent(this, PayPwdGuideSetActivity.class);
            intent.putExtra(Constants.LOCAL_FROM_KEY, Constants.LOCAL_PAYMENT_FROM_GUIDE_SET);
            startActivityForResult(intent, REQUEST_PARAM_INT_CODE);
        }
    }

    private void onCheckPayPwd() {
        AccountSdk.checkPayPwd(this, GlobalApplication.getApplication().getSID(), AccountSdk.PWD_CHECK_TTYPE_MODIFY, new AccountSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                CheckPayPwdRsp resp = CheckPayPwdRsp.decodeJson(CheckPayPwdRsp.class, jsonObject);
                if (TextUtils.equals(code, AccountSdk.LOCAL_PAY_SUCCESS)
                        && resp != null) {
                    Intent intent = new Intent(SettingsActivity.this, PayPwdGuideSetActivity.class);
                    intent.putExtra(Constants.LOCAL_FROM_KEY, Constants.LOCAL_PAYMENT_FROM_RESET);
                    intent.putExtra(Constants.LOCAL_TOKEN_KEY, resp.getToken());
                    startActivity(intent);
                } else {
                    showToast(errorMsg);
                    LogUtil.e(TAG, errorMsg);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PARAM_INT_CODE && resultCode == 0) {
            String status = ProfileUserUtil.getInstance().getAccountStatus();
            if (TextUtils.equals(status, Constants.STATUE_SAFE_KEY)) {
                tv_set_pay_pwd.setText(R.string.Setting_C0);
            } else {
                tv_set_pay_pwd.setText(R.string.Setting_D0);
            }
        }
    }

    /**
     * 登出接口调用之后再清理sid, 否则iid无法解绑
     */
    private void onLogout() {
        AppMain.getInstance().onLogout(this, GlobalApplication.getApplication().getSID(), new UserSdkCallBack() {

            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
            }
        });
        CleanConfigUtil.cleanAllConfig();//清理登录信息
        ActivityManager.getInstance().openLoginActivity(this);
    }

    private void onBack() {
        if (dialog != null && dialog.isShowing()) {
            onCloseDialog();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBack();
    }

    private void onCloseDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        dialog = null;
        loadingDialog = null;
    }

    private void openTouchID() {
        startActivity(new Intent(this, TouchIDActivity.class));
    }
}
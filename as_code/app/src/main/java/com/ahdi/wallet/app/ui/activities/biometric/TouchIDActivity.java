package com.ahdi.wallet.app.ui.activities.biometric;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ahdi.wallet.app.sdk.AccountSdk;
import com.ahdi.wallet.app.sdk.OtherSdk;
import com.ahdi.wallet.app.callback.AccountSdkCallBack;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.fingerprint.FingerprintSDK;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;
import com.ahdi.lib.utils.widgets.CheckSafety;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.callback.AppConfigCallBack;
import com.ahdi.wallet.app.callback.OpenTouchIDCallback;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.main.TouchIDManager;
import com.ahdi.wallet.app.ui.activities.payPwd.PayPwdGuideSetActivity;
import com.ahdi.wallet.app.ui.base.BaseClickableSpan;
import com.ahdi.wallet.util.AppConfigHelper;

import org.json.JSONObject;


/**
 * Date: 2019/1/4 上午10:40
 * Author: kay lau
 * Description:
 */
public class TouchIDActivity extends AppBaseActivity {

    private static final String TAG = TouchIDActivity.class.getSimpleName();
    private static final int CLOSE_TOUCHID_PAYMENT = 0;
    private static final int CLOSE_TOUCHID_UNLOCK = 1;

    private CheckBox cb_switch_touchID_pay, cb_switch_touchID_unlock;
    private SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initSpannableText();
        setContentView(R.layout.activity_touch_id);
        initCommonTitle(getString(R.string.TouchID_A0));
        initView();
        initData();
    }

    private void initSpannableText() {
        SpannableString prefix = new SpannableString(getString(R.string.TouchID_B0));
        ForegroundColorSpan color = new ForegroundColorSpan(getResources().getColor(R.color.CC_919399));
        prefix.setSpan(color, 0, prefix.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append(prefix);

        SpannableString textClick = new SpannableString(getString(R.string.TouchID_C0));
        BaseClickableSpan baseClickableSpan = new BaseClickableSpan(this,
                new BaseClickableSpan.OnClickableSpaListener() {
                    @Override
                    public void onClick(View widget) {
                        getAgreement();
                    }
                });

        baseClickableSpan.setTextColor(getResources().getColor(R.color.CC_D63031));
        baseClickableSpan.setUnderlineText(true);
        textClick.setSpan(baseClickableSpan, 0, textClick.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append(textClick);
    }

    private void initView() {
        TextView tv_touch_id_tips = findViewById(R.id.tv_touch_id_tips);
        tv_touch_id_tips.setHighlightColor(getResources().getColor(android.R.color.transparent));
        tv_touch_id_tips.setMovementMethod(LinkMovementMethod.getInstance());
        tv_touch_id_tips.setText(spannableStringBuilder);

        findViewById(R.id.ll_switch_touchid_unlock).setOnClickListener(this);
        findViewById(R.id.ll_switch_touchid_pay).setOnClickListener(this);
        cb_switch_touchID_pay = findViewById(R.id.cb_switch_touchid_pay);
        cb_switch_touchID_unlock = findViewById(R.id.cb_switch_touchid_unlock);
    }

    private void initData() {
        initSwitchState();
    }

    private void initSwitchState() {
        String LName = AppGlobalUtil.getInstance().getString(this, Constants.LOCAL_LNAME_KEY);
        boolean isOpenTouchIDUnlock = TouchIDStateUtil.isStartTouchIDUnlock(this, LName);
        boolean isOpenTouchIDPay = TouchIDStateUtil.isStartTouchIDPayment(this, LName);

        cb_switch_touchID_unlock.setChecked(false);
        cb_switch_touchID_pay.setChecked(false);
        if (FingerprintSDK.isHasFingerprints(this)) {
            if (isOpenTouchIDUnlock) {
                cb_switch_touchID_unlock.setChecked(true);
            }
            if (isOpenTouchIDPay) {
                cb_switch_touchID_pay.setChecked(true);
            }
        } else if (isOpenTouchIDUnlock) {
            showErrorDialog(getString(R.string.DialogMsg_Y0));
            TouchIDStateUtil.clearTouchIDUnlockData(this, LName);

        } else if (isOpenTouchIDPay) {
            showErrorDialog(getString(R.string.DialogMsg_Y0));
            TouchIDStateUtil.clearTouchIDPayData(this, LName);
        }
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.ll_switch_touchid_pay) {
            onCheckPayPwd();

        } else if (id == R.id.ll_switch_touchid_unlock) {
            onOpenTouchIDUnlock();
        }
    }

    /**
     * 开启/关闭指纹解锁
     */
    private void onOpenTouchIDUnlock() {
        if (!cb_switch_touchID_unlock.isChecked()) {
            if (FingerprintSDK.isHasFingerprints(this)) {
                String lName = AppGlobalUtil.getInstance().getLName(this);
                TouchIDManager.getInstance().onStartTouchIDUnlock(this, lName, new OpenTouchIDCallback() {
                    @Override
                    public void onCallback(int code) {
                        boolean state = code == FingerprintSDK.CODE_SUCCESS;
                        cb_switch_touchID_unlock.setChecked(state);
                    }
                });
            } else {
                showErrorDialog(getString(R.string.DialogMsg_I0));
            }
        } else {
            showCloseTouchIDDialog(getString(R.string.DialogMsg_V0), CLOSE_TOUCHID_UNLOCK);
        }
    }

    private void showCloseTouchIDDialog(String msg, int type) {
        new CommonDialog
                .Builder(this)
                .setMessage(msg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setNegativeButton(getString(R.string.DialogButton_A0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        String LName = AppGlobalUtil.getInstance().getLName(TouchIDActivity.this);
                        if (type == CLOSE_TOUCHID_PAYMENT) {
                            cb_switch_touchID_pay.setChecked(TouchIDStateUtil.isStartTouchIDPayment(TouchIDActivity.this, LName));

                        } else if (type == CLOSE_TOUCHID_UNLOCK) {
                            cb_switch_touchID_unlock.setChecked(TouchIDStateUtil.isStartTouchIDUnlock(TouchIDActivity.this, LName));
                        }
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getString(R.string.DialogButton_E0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (type == CLOSE_TOUCHID_PAYMENT) {
                            onCloseTouchIDPayment();

                        } else if (type == CLOSE_TOUCHID_UNLOCK) {
                            onCloseTouchIDUnlock();
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

    private void onCloseTouchIDPayment() {
        cb_switch_touchID_pay.setChecked(false);
        TouchIDManager.getInstance().closePayFinger(this);
        ToastUtil.showToastCustom(this, getString(R.string.Toast_A1));
        AccountSdk.closeTouchIDPay(GlobalApplication.getApplication().getSID(), new AccountSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                LogUtil.e(TAG, code + " = " + errorMsg);
            }
        });
        LogUtil.e(TAG, "------手动关闭指纹支付------");
    }

    private void onCloseTouchIDUnlock() {
        cb_switch_touchID_unlock.setChecked(false);
        TouchIDManager.getInstance().closeLoginFinger(this);
        ToastUtil.showToastCustom(this, getString(R.string.Toast_A1));
        LogUtil.e(TAG, "------手动关闭指纹解锁------");
    }

    /**
     * 开启/关闭指纹支付---验证支付密码
     */
    private void onCheckPayPwd() {
        if (!cb_switch_touchID_pay.isChecked()) {
            // 是否包含至少一个指纹
            if (!FingerprintSDK.isHasFingerprints(this)) {
                showErrorDialog(getString(R.string.DialogMsg_I0));
                return;
            }
            // 判断支付密码是否设置
            if (!CheckSafety.checkSafeTouchID(this, PayPwdGuideSetActivity.class)) {
                return;
            }
            // 验证支付密码
            AccountSdk.checkPayPwd(this, GlobalApplication.getApplication().getSID(),
                    AccountSdk.PWD_CHECK_TTYPE_NOT, new AccountSdkCallBack() {
                        @Override
                        public void onResult(String code, String errMsg, JSONObject jsonObject) {
                            if (TextUtils.equals(code, AccountSdk.LOCAL_PAY_SUCCESS)
                                    && jsonObject != null) {
                                String payPwdCipher = jsonObject.optString(Constants.LOCAL_pwd_KEY);
                                String random = jsonObject.optString(Constants.LOCAL_random_KEY);
                                onVerifyFingerprint(payPwdCipher, random);
                            } else {
                                LogUtil.e(TAG, errMsg);
                            }
                        }
                    });
        } else {
            showCloseTouchIDDialog(getString(R.string.DialogMsg_P0), CLOSE_TOUCHID_PAYMENT);
        }
    }

    /**
     * 开启指纹支付
     *
     * @param payPwdCipher
     * @param random
     */
    private void onVerifyFingerprint(String payPwdCipher, String random) {
        TouchIDManager.getInstance().onStartVerifyFingerprint(this, payPwdCipher, random, new OpenTouchIDCallback() {
            @Override
            public void onCallback(int code) {
                cb_switch_touchID_pay.setChecked(code == FingerprintSDK.CODE_SUCCESS);
            }
        });
    }

    /**
     * 获取协议地址
     */
    private void getAgreement() {
        LoadingDialog loadingDialog = LoadingDialog.showDialogLoading(this, getString(com.ahdi.lib.utils.R.string.DialogTitle_C0));
        AppMain.getInstance().appConfig(this, new AppConfigCallBack() {
            @Override
            public void onResult(String Code, String errorMsg) {
                LoadingDialog.dismissDialog(loadingDialog);
                if (TextUtils.equals(Code, OtherSdk.LOCAL_SUCCESS)) {
                    openWebCommonActivity(AppConfigHelper.getInstance().getTouchAgreementUrl());

                } else {
                    showToast(errorMsg);
                }
            }
        });
    }

}

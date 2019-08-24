package com.ahdi.wallet.app.ui.activities.payPwd;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.lib.utils.fingerprint.FingerprintSDK;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.bean.UserData;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;
import com.ahdi.lib.utils.widgets.keyboard.KeyboardUtil;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.ui.activities.balance.BalanceActivity;
import com.ahdi.wallet.app.ui.activities.biometric.GuideTouchIDPayActivity;

public class PayPwdGuideSetActivity extends AppBaseActivity {

    public static final String TAG = PayPwdGuideSetActivity.class.getSimpleName();
    public static final int REQUEST_RESET_PAY_PWD = 901;
    public static final int RESULT_RESET_PAY_PWD = 902;
    public static final int RESULT_RESET_PAY_PWD_BACK = 903;
    private EditText edt_pwd;
    private ImageView[] imageViews;
    private KeyboardUtil keyboardUtil;
    private int from;
    private String token;
    private TextView tv_title;
    private TextView tv_tip_one, tv_tip_two, tv_tip_three;
    private String random;
    private String payPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData();
        setContentView(R.layout.activity_pay_pwd_guide_set);
        tv_title = initTitleTextView("");
        initTitleBack().setOnClickListener(this);
        initView();
    }

    private void initIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            from = intent.getIntExtra(Constants.LOCAL_FROM_KEY, 0);
            token = intent.getStringExtra(Constants.LOCAL_TOKEN_KEY);
        }
    }

    public void initView() {

        View view = findViewById(R.id.check_pay_pwd_layout);
        tv_tip_one = view.findViewById(R.id.tv_tip_one);
        tv_tip_two = view.findViewById(R.id.tv_tip_two);
        tv_tip_three = view.findViewById(R.id.tv_tip_three);

        View pwd_layout = view.findViewById(R.id.pwd_layout);
        edt_pwd = pwd_layout.findViewById(R.id.et);
        edt_pwd.setCursorVisible(false);
        imageViews = new ImageView[6];
        imageViews[0] = pwd_layout.findViewById(R.id.item_code_iv1);
        imageViews[1] = pwd_layout.findViewById(R.id.item_code_iv2);
        imageViews[2] = pwd_layout.findViewById(R.id.item_code_iv3);
        imageViews[3] = pwd_layout.findViewById(R.id.item_code_iv4);
        imageViews[4] = pwd_layout.findViewById(R.id.item_code_iv5);
        imageViews[5] = pwd_layout.findViewById(R.id.item_code_iv6);

        intKeyBoardView();
        initData();
    }

    private void initData() {
        if (from == Constants.LOCAL_PAYMENT_FROM_GUIDE_SET) {
            tv_title.setText(getString(R.string.GuideSetPayPWDPage1_A0));
            tv_tip_one.setText(getString(R.string.GuideSetPayPWDPage1_B0));

        } else if (from == Constants.LOCAL_PAYMENT_FROM_RESET) {
            tv_title.setText(getString(R.string.ModifyPayPWDPage1_A0));
            tv_tip_one.setText(getString(R.string.ModifyPayPWDPage1_B0));
        }
        UserData userData = ProfileUserUtil.getInstance().getUserData();
        if (userData != null) {
            String nName = userData.getNName();
            String lName = userData.getsLName();
            tv_tip_two.setText(nName);
            tv_tip_three.setText("(".concat(lName).concat(")"));
            tv_tip_two.setVisibility(View.VISIBLE);
            tv_tip_three.setVisibility(View.VISIBLE);
        }

    }

    public void intKeyBoardView() {
        keyboardUtil = new KeyboardUtil(this, edt_pwd, 2, new PayPwdGuideSetActivity.KeyboardCallback());
        keyboardUtil.showKeyboard();
    }

    @Override
    public void onClick(View view) {
        if (!isCanClick()) {
            return;
        }
        if (view.getId() == R.id.btn_back) {
            onBackPressed();
        }
    }

    private class KeyboardCallback implements KeyboardUtil.CallBack {

        @Override
        public void onCallback(String result) {
            if (onPayPwdCheck(result)) {
                openModifyPwd(result);

            } else {
                onClearKeyboard();
            }
        }

        @Override
        public void onPwdInputCallback(StringBuffer buffer, String pwdStr) {
            for (int i = 0; i < buffer.length(); i++) {
                ImageView imageView = imageViews[i];
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.password_black_point);
            }
        }

        @Override
        public void onPwdStrCallback(StringBuffer stringBuffer) {
            imageViews[stringBuffer.length()].setVisibility(View.INVISIBLE);
        }
    }

    private void openModifyPwd(String result) {
        Intent intent = new Intent(this, PayPwdModifyActivity.class);
        intent.putExtra(Constants.LOCAL_pwd_KEY, result);
        intent.putExtra(Constants.LOCAL_TOKEN_KEY, token);
        intent.putExtra(Constants.LOCAL_FROM_KEY, from);
        startActivityForResult(intent, REQUEST_RESET_PAY_PWD);
    }

    public boolean onPayPwdCheck(String pwd) {
        int t0 = Integer.parseInt(pwd.substring(0, 1));
        int t1 = Integer.parseInt(pwd.substring(1, 2));
        int t2 = Integer.parseInt(pwd.substring(2, 3));
        int t3 = Integer.parseInt(pwd.substring(3, 4));
        int t4 = Integer.parseInt(pwd.substring(4, 5));
        int t5 = Integer.parseInt(pwd.substring(5, 6));
        int c0 = Math.abs(t1 - t0);
        int c1 = Math.abs(t2 - t1);
        int c2 = Math.abs(t3 - t2);
        int c3 = Math.abs(t4 - t3);
        int c4 = Math.abs(t5 - t4);
        boolean isContinue = (c0 == 1 && c1 == 1 && c2 == 1 && c3 == 1 && c4 == 1);
        boolean isSame = (t0 == t1 && t0 == t2 && t0 == t3 && t0 == t4 && t0 == t5);
        if (isContinue) {
            showToast(getString(R.string.Toast_J0));
            return false;
        } else if (isSame) {
            showToast(getString(R.string.Toast_K0));
            return false;
        }
        onClearKeyboard();
        return true;
    }

    private void onClearKeyboard() {
        if (keyboardUtil != null) {
            keyboardUtil.onDeleteAll();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_RESET_PAY_PWD == requestCode) {
            if (RESULT_RESET_PAY_PWD == resultCode) {
                String LName = AppGlobalUtil.getInstance().getLName(this);
                // 密码成功设置/修改
                // 如果是引导设置的支付密码, 则引导开启指纹支付
                if (from == Constants.LOCAL_PAYMENT_FROM_GUIDE_SET
                        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && FingerprintSDK.isSupport(PayPwdGuideSetActivity.this)
                        && FingerprintSDK.isHasFingerprints(PayPwdGuideSetActivity.this)
                        && !TouchIDStateUtil.isSkipGuidePay(this, LName)) {
                    if (data != null) {
                        payPwd = data.getStringExtra(Constants.LOCAL_pwd_KEY);
                        random = data.getStringExtra(Constants.LOCAL_random_KEY);
                    }
                    onGuideSetTouchID();

                } else {
                    setResult(BalanceActivity.RESULT_CODE_CHECK_SAFETY);
                }
                finish();
            } else if (RESULT_RESET_PAY_PWD_BACK == resultCode) {
                finish();
            }
            // 新密码旧密码相同A207返回时, 停留当前页面, 不关闭
        }
    }

    private void onGuideSetTouchID() {
        Intent intent = new Intent(this, GuideTouchIDPayActivity.class);
        intent.putExtra(Constants.LOCAL_random_KEY, random);
        intent.putExtra(Constants.LOCAL_pwd_KEY, payPwd);
        intent.putExtra(Constants.LOCAL_FROM_KEY, Constants.LOCAL_PAYMENT_FROM_GUIDE_SET);
        startActivityForResult(intent, REQUEST_RESET_PAY_PWD);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}

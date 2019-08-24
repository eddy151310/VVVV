package com.ahdi.wallet.app.ui.activities.biometric;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.fingerprint.FingerprintSDK;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.callback.OpenTouchIDCallback;
import com.ahdi.wallet.app.main.TouchIDManager;

/**
 * Date: 2019/3/26 上午9:13
 * Author: kay lau
 * Description:
 */
public class GuideTouchIDPayActivity extends AppBaseActivity {

    private static final String TAG = GuideTouchIDPayActivity.class.getSimpleName();
    private CheckBox cb_next_skip;
    private String payPwdCipher;
    private String random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData(getIntent());
        setContentView(R.layout.activity_guide_touch_id_pay);
        initView();
    }

    private void initIntentData(Intent intent) {
        if (intent == null) {
            return;
        }
        payPwdCipher = intent.getStringExtra(Constants.LOCAL_pwd_KEY);
        random = intent.getStringExtra(Constants.LOCAL_random_KEY);
    }

    private void initView() {
        findViewById(R.id.rl_guide_pay_skip).setOnClickListener(this);
        findViewById(R.id.iv_verify_touchID).setOnClickListener(this);
        cb_next_skip = findViewById(R.id.cb_next_skip);
        cb_next_skip.setOnClickListener(this);
        onVerifyFingerprint(payPwdCipher, random);
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.rl_guide_pay_skip) {
            onSkip();

        } else if (id == R.id.iv_verify_touchID) {
            onVerifyFingerprint(payPwdCipher, random);

        } else if (id == R.id.cb_next_skip) {
            if (cb_next_skip.isChecked()) {
                cb_next_skip.setChecked(true);
            } else {
                cb_next_skip.setChecked(false);
            }
        }
    }

    private void onSkip() {
        String lname = AppGlobalUtil.getInstance().getLName(this);
        if (cb_next_skip.isChecked()) {
            // 跳过, 下次不再提示引导
            TouchIDStateUtil.setGuidePayState(this, lname, true);
        } else {
            TouchIDStateUtil.setGuidePayState(this, lname, false);
        }
        finish();
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
                if (code == FingerprintSDK.CODE_SUCCESS) {
                    finish();
                }
                LogUtil.e(TAG, "引导开启指纹是否成功: " + (code == FingerprintSDK.CODE_SUCCESS));
            }
        });
    }
}

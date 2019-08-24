package com.ahdi.wallet.app.ui.activities.biometric;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.fingerprint.FingerprintSDK;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.callback.OpenTouchIDCallback;
import com.ahdi.wallet.app.main.TouchIDManager;

/**
 * Date: 2019/1/7 下午5:57
 * Author: kay lau
 * Description:
 */
public class GuideTouchIDUnlockActivity extends AppBaseActivity {

    public static final String TAG = GuideTouchIDUnlockActivity.class.getSimpleName();

    public static final int OPEN_REQUEST_CODE = 1001;
    public static final int CLOSE_RESULT_CODE = 1002;
    private CheckBox cb_next_skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_guide_touch_id_unlock);
        initView();
    }

    private void initView() {
        findViewById(R.id.rl_guide_unlock_skip).setOnClickListener(this);
        findViewById(R.id.iv_verify_touchID).setOnClickListener(this);
        cb_next_skip = findViewById(R.id.cb_next_skip);
        cb_next_skip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.rl_guide_unlock_skip) {
            onSkip();

        } else if (id == R.id.iv_verify_touchID) {
            startTouchIDUnlock();

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
            TouchIDStateUtil.setGuideUnlockState(this, lname, true);
        } else {
            TouchIDStateUtil.setGuideUnlockState(this, lname, false);
        }
        setResult(CLOSE_RESULT_CODE);
        finish();
    }

    private void startTouchIDUnlock() {
        String lName = AppGlobalUtil.getInstance().getLName(this);
        TouchIDManager.getInstance().onStartTouchIDUnlock(this, lName, new OpenTouchIDCallback() {
            @Override
            public void onCallback(int code) {
                if (code == FingerprintSDK.CODE_SUCCESS) {
                    setResult(CLOSE_RESULT_CODE);
                    finish();
                }
            }
        });
    }
}

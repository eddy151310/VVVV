package com.ahdi.wallet.app.ui.activities.biometric;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.fingerprint.FingerprintSDK;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.CleanConfigUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ScreenAdaptionUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.callback.OpenTouchIDCallback;
import com.ahdi.wallet.app.main.TouchIDManager;

/**
 * Date: 2019/1/4 下午3:11
 * Author: kay lau
 * Description:
 */
public class TouchIDLoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = TouchIDLoginActivity.class.getSimpleName();
    private ImageView iv_user_icon;
    private TextView tv_nname, tv_sLname;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenAdaptionUtil.setCustomDensity(this, getApplication());
        setContentView(R.layout.activity_verify_touch_id_login);
        initTitle();
        initView();
        initData();
    }

    private void initTitle() {
        View titleView = findViewById(R.id.title);
        titleView.setBackgroundResource(R.color.CC_00000000);
        //初始化控件
        Button btn_back = findViewById(R.id.btn_back);
        btn_back.setBackgroundColor(Color.TRANSPARENT);
        btn_back.setText(getString(R.string.BiometryLogin_A0));
        btn_back.setTextColor(getResources().getColor(R.color.CC_282934));
        btn_back.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size_17));
        btn_back.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.common_mbm_nav_logo), null, null, null);
        btn_back.setCompoundDrawablePadding(5);
        //设置距左R.dimen.dp_10
        RelativeLayout.LayoutParams layoutParams;
        layoutParams = (RelativeLayout.LayoutParams) btn_back.getLayoutParams();
        layoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.dp_10), 0, 0, 0);//4个参数按顺序分别是左上右下
        btn_back.setLayoutParams(layoutParams);
    }

    private void initView() {
        iv_user_icon = findViewById(R.id.iv_user_icon);
        tv_nname = findViewById(R.id.tv_nname);
        tv_sLname = findViewById(R.id.tv_sLname);
        findViewById(R.id.iv_verify_touchID).setOnClickListener(this);
        findViewById(R.id.tv_other_login).setOnClickListener(this);
    }

    private void initData() {
        String LName = AppGlobalUtil.getInstance().getString(this, Constants.LOCAL_LNAME_KEY);
        String userIconUrl = TouchIDStateUtil.getUnlockLoginAvatar(this, LName);
        ImageDownUtil.downMySelfPhoto(this, userIconUrl, iv_user_icon);
        String nname = AppGlobalUtil.getInstance().getString(this, Constants.LOCAL_NNAME_KEY);
        tv_nname.setText(nname);
        String sLname = AppGlobalUtil.getInstance().getString(this, Constants.LOCAL_SLNAME_KEY);
        tv_sLname.setText(sLname);
    }

    @Override
    public void onClick(View v) {
        if (!ToolUtils.isCanClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.iv_verify_touchID) {
            checkVerifyTouchIDLogin();

        } else if (id == R.id.tv_other_login) {
            // 清空该用户登录凭证。下次进入时，不会触发指纹解锁自动登陆。
            CleanConfigUtil.cleanVoucher();
            onPINLogin();
        }
    }

    private void onPINLogin() {
        ActivityManager.getInstance().openLoginActivity(this);
        overridePendingTransition(R.anim.in_from_right, 0);
    }

    private void checkVerifyTouchIDLogin() {
        if (!FingerprintSDK.isHasFingerprints(this)) {
            // 设备没有录入指纹, 弹出dialog之前, 就需要清除 DC  指纹标记等
            showLoginDialog(getString(R.string.DialogMsg_A1));

        } else {
            onVerifyTouchIDLogin();
        }
    }

    /**
     * 验证指纹解锁
     */
    private void onVerifyTouchIDLogin() {
        String lName = AppGlobalUtil.getInstance().getLName(this);
        TouchIDManager.getInstance().onVerifyTouchIDUnlock(this, lName, new OpenTouchIDCallback() {
            @Override
            public void onCallback(int code) {
                if (code == FingerprintSDK.CODE_SUCCESS) {
                    LogUtil.e(TAG, "------验证指纹解锁成功------ [打开首页自动登录]");
                    ActivityManager.getInstance().openMainActivity(TouchIDLoginActivity.this);

                } else if (code == FingerprintSDK.CODE_0) {
                    // 指纹验证失败3次, 吐司提示
                    ToastUtil.showToastShort(TouchIDLoginActivity.this, getString(R.string.Toast_B1));

                } else if (code == FingerprintSDK.CODE_5) {
                    // 指纹验证失败5次系统锁定
                    showErrorDialog(getString(R.string.DialogMsg_U0));

                } else if (code == FingerprintSDK.CODE_8) {
                    // 指纹认证存在安全漏洞，重新登录 (此种情况模拟器无法测试, 需用真机测试)
                    // 弹出dialog之前 就需要清除 DC  指纹标记等
                    showLoginDialog(getString(R.string.DialogMsg_Z0));
                }
            }
        });
    }

    private void showErrorDialog(String msg) {
        new CommonDialog
                .Builder(this)
                .setMessage(msg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.DialogButton_B0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * 去登录
     *
     * @param msg
     */
    private void showLoginDialog(String msg) {
        // 弹出dialog之前, 就需要清除 DC  指纹标记等
        CleanConfigUtil.cleanVoucher();
        TouchIDManager.getInstance().closeLoginFinger(this);
        new CommonDialog
                .Builder(this)
                .setMessage(msg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.DialogButton_I0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onPINLogin();
                        finish();
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkVerifyTouchIDLogin();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FingerprintSDK.cancelFingerprintRecognition();
    }
}

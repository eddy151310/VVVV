package com.ahdi.wallet.app.ui.activities.other;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ahdi.wallet.app.sdk.OtherSdk;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.utils.DeviceUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.callback.AppConfigCallBack;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.util.AppConfigHelper;

public class AboutActivity extends AppBaseActivity {

    private static final String TAG = AboutActivity.class.getSimpleName();

    private LoadingDialog loadingDialog = null;
    private TextView tv_app_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_about);
        initCommonTitle(getString(R.string.About_A0));
        initView();
        initData();
    }

    public void initView() {
        tv_app_version = findViewById(R.id.tv_app_version);
        findViewById(R.id.rl_update).setOnClickListener(this);
        findViewById(R.id.rl_agreement).setOnClickListener(this);
    }

    private void initData() {
        tv_app_version.setText(getString(R.string.app_name) + " v" + DeviceUtil.getVersionName(this));
    }

    @Override
    public void onClick(View view) {
        if (!isCanClick()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.rl_update) {
            goMarket();
        } else if (id == R.id.rl_agreement) {
            openAboutUrl();
        }
    }

    /**
     * 跳到应用市场
     */
    private void goMarket() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
        //存在手机里没安装应用市场的情况，跳转会包异常，做一个接收判断
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else { //没有应用市场，我们通过浏览器跳转到Google Play
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                LogUtil.d(TAG, "既没有应用市场也没有浏览器");
            }
        }
    }

    /**
     * 更新配置
     */
    private void openAboutUrl() {
        loadingDialog = showLoading();
        AppMain.getInstance().appConfig(this, new AppConfigCallBack() {
            @Override
            public void onResult(String Code, String errorMsg) {
                closeLoading(loadingDialog);
                if (TextUtils.equals(Code, OtherSdk.LOCAL_SUCCESS)) {
                    openWebCommonActivity(AppConfigHelper.getInstance().getUserAgreeMentUrl());
                } else {
                    showToast(errorMsg);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        loadingDialog = null;
    }

}
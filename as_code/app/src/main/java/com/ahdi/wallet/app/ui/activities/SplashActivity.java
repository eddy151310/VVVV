package com.ahdi.wallet.app.ui.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.DeviceUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ScreenAdaptionUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;
import com.ahdi.lib.utils.widgets.PagerIndicatorView;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.ui.activities.biometric.TouchIDLoginActivity;
import com.ahdi.wallet.app.ui.adapters.GuideAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Date: 2018/6/5 下午12:09
 * Author: kay lau
 * Description:
 */
public class SplashActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "SplashActivity";

    private ViewPager viewPager;
    private PagerIndicatorView indicatorView;
    private Button btn_confirm;

    /**
     * app version name
     */
    private String versionName = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initChannel();
        super.onCreate(savedInstanceState);
        ScreenAdaptionUtil.setCustomDensity(this, getApplication());
        setContentView(R.layout.activity_splash);
        initNotificationChannel();
        if (isGuide()) {
            initView();
            initData();
            LogUtil.d(TAG, "开启引导页");
        } else {
            goToLogin();
        }
    }
    private void initChannel() {
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String channel = info.metaData.getString("CHANNEL");
            LogUtil.e("tag", ":channel: " + channel);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否需要做首页引导
     *
     * @return
     */
    private boolean isGuide() {
        versionName = DeviceUtil.getVersionName(SplashActivity.this);
        String version = AppGlobalUtil.getInstance().getString(SplashActivity.this, Constants.LOCAL_KEY_GUIDE_PAGER);
        if (TextUtils.isEmpty(version) || !TextUtils.equals(version, versionName)) {
            return true;
        }
        return false;
    }

    private void initView() {
        findViewById(R.id.rl_layout).setVisibility(View.VISIBLE);
        viewPager = findViewById(R.id.view_pager);
        indicatorView = findViewById(R.id.pager_indicator);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
    }

    private void initData() {
        //增加或者减少引导页
        List<Drawable> drawables = new ArrayList<>();
        Resources resources = getResources();
        drawables.add(resources.getDrawable(R.mipmap.guide_page_one));

        if (drawables.size() == 1) {
            indicatorView.setVisibility(View.GONE);
            btn_confirm.setVisibility(View.VISIBLE);
        }
        int endPosition = drawables.size() - 1;

        viewPager.setAdapter(new GuideAdapter(SplashActivity.this, drawables));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                indicatorView.onPageScrolled(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                indicatorView.onPageSelected(position);
                if (position == endPosition) {
                    indicatorView.setVisibility(View.GONE);
                    btn_confirm.setVisibility(View.VISIBLE);
                } else {
                    indicatorView.setVisibility(View.VISIBLE);
                    btn_confirm.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!ToolUtils.isCanClick()) {
            return;
        }
        if (v.getId() == R.id.btn_confirm) {
            AppGlobalUtil.getInstance().putString(SplashActivity.this, Constants.LOCAL_KEY_GUIDE_PAGER, versionName);
            LogUtil.d(TAG, "点击引导页的进入app按钮");
            goToLogin();
        }
    }

    private void goToLogin() {
        //判断sid是否存在，执行自动登录
        String sid = GlobalApplication.getApplication().getSID();
        if (!TextUtils.isEmpty(sid)) {
            ActivityManager.getInstance().openMainActivity(this);
        } else {
            ActivityManager.getInstance().openLoginActivity(this);
        }
        finish();
    }

    /**
     * android 8.0开始使用消息通知，必须创建渠道
     */
    public void initNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(Constants.NOTIFICATION_CHANNEL, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
}

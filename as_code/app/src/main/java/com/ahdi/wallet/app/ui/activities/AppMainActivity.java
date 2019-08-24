package com.ahdi.wallet.app.ui.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.wallet.app.sdk.AccountSdk;
import com.ahdi.wallet.app.callback.AccountSdkCallBack;
import com.ahdi.wallet.app.response.ProfileRsp;
import com.ahdi.wallet.app.schemas.AccountSchema;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AmountUtil;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.ui.fragments.HomeFragment;
import com.ahdi.wallet.app.ui.fragments.MeFragment;
import com.ahdi.wallet.app.ui.fragments.TestFragment;

import org.json.JSONObject;

/**
 * Date: 2017/12/27 上午10:24
 * Author: kay lau
 * Description:
 */
public class AppMainActivity extends AppBaseActivity implements HomeFragment.OnUpdateProfileListener {

    private static final int SHOW_TEST = 0;
    private static final int SHOW_HOME = 1;
    private static final int SHOW_ME = 2;

    private View ll_test ,ll_home, ll_me;

    private ImageView iv_test ,iv_home, iv_me;

    private TextView tv_test,tv_home, tv_me;

    private TestFragment testFragment;
    private HomeFragment homeFragment;
    private MeFragment meFragment;

    private FragmentManager mFragmentManager;//fragment管理者
    private FragmentTransaction mFragmentTransaction;//fragment事务
    private boolean isSendProfileHttpTask;//是否已发送请求
    private String from;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isRecreateActivity() && isMemoryRecover(savedInstanceState)) {
            return;
        }
        initIntentData(getIntent());

        setContentView(R.layout.activity_layout_app_main);
        initView();

        initData();

        showTestFragment();
    }

    private void initIntentData(Intent intent) {
        if (intent == null) {
            return;
        }
        from = intent.getStringExtra(Constants.LOCAL_FROM_KEY);
    }

    private void initView() {
        mFragmentManager = getFragmentManager();//获取到fragment的管理对象

        ll_test = findViewById(R.id.ll_test);
        ll_home = findViewById(R.id.ll_home);
        ll_me = findViewById(R.id.ll_me);

        iv_test = findViewById(R.id.iv_test);
        iv_home = findViewById(R.id.iv_home);
        iv_me = findViewById(R.id.iv_me);

        tv_test = findViewById(R.id.tv_test);
        tv_home = findViewById(R.id.tv_home);
        tv_me = findViewById(R.id.tv_me);
    }

    private void initData() {

        ll_test.setOnClickListener(this);
        ll_home.setOnClickListener(this);
        ll_me.setOnClickListener(this);

        iv_test.setOnClickListener(this);
        iv_home.setOnClickListener(this);
        iv_me.setOnClickListener(this);

        isStatisticActivity = false;
    }

    /**
     * 是不是重新创建页面
     *
     * @return
     */
    private boolean isRecreateActivity() {
        return !TextUtils.isEmpty(AppGlobalUtil.getInstance().getString(AppGlobalUtil.getInstance().getContext(), Constants.LOCAL_RECREATE_APP_MAIN_ACTIVITY));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_test:
            case R.id.iv_test:
                if (testFragment != null && !testFragment.isHidden()) {
                    return;
                }
                showTestFragment();
                break;
            case R.id.ll_home:
            case R.id.iv_home:
                if (homeFragment != null && !homeFragment.isHidden()) {
                    return;
                }
                showHomeFragment();
                break;
            case R.id.ll_me:
            case R.id.iv_me:
                if (meFragment != null && !meFragment.isHidden()) {
                    return;
                }
                showMeFragment();
                break;
            default:
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRecreateActivity()) {
            if (mFragmentManager == null) {
                mFragmentManager = getFragmentManager();
            }
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            if (testFragment != null) {
                fragmentTransaction.remove(testFragment);
            }

            if (homeFragment != null) {
                fragmentTransaction.remove(homeFragment);
            }

            if (meFragment != null) {
                fragmentTransaction.remove(meFragment);
            }
            fragmentTransaction.commitAllowingStateLoss();
            recreate();
        } else {
            sendProfileHttpTask();
        }
    }

    //按钮颜色
    private void showTestFragment() {
        sendProfileHttpTask();

        iv_test.setBackgroundResource(R.mipmap.main_home_selected);
        iv_home.setBackgroundResource(R.mipmap.main_home_normal);
        iv_me.setBackgroundResource(R.mipmap.main_me_normal);

        tv_test.setTextColor(ToolUtils.getColor(this, R.color.CC_D63031));
        tv_home.setTextColor(ToolUtils.getColor(this, R.color.CC_919399));
        tv_me.setTextColor(ToolUtils.getColor(this, R.color.CC_919399));
        setClick(SHOW_TEST);
    }

    private void showHomeFragment() {
        sendProfileHttpTask();
        iv_home.setBackgroundResource(R.mipmap.main_home_selected);
        iv_me.setBackgroundResource(R.mipmap.main_me_normal);
        tv_home.setTextColor(ToolUtils.getColor(this, R.color.CC_D63031));
        tv_me.setTextColor(ToolUtils.getColor(this, R.color.CC_919399));
        setClick(SHOW_HOME);
    }

    private void showMeFragment() {
        sendProfileHttpTask();
        iv_home.setBackgroundResource(R.mipmap.main_home_normal);
        iv_me.setBackgroundResource(R.mipmap.main_me_selected);
        tv_home.setTextColor(ToolUtils.getColor(this, R.color.CC_919399));
        tv_me.setTextColor(ToolUtils.getColor(this, R.color.CC_D63031));
        setClick(SHOW_ME);
    }

    private void setClick(int type) {
        if (mFragmentManager == null) {
            mFragmentManager = getFragmentManager();
        }
        //开启事务
        mFragmentTransaction = mFragmentManager.beginTransaction();
        //显示之前将所有的fragment都隐藏起来,在去显示我们想要显示的fragment
        hideFragment(mFragmentTransaction);
        switch (type) {

            case SHOW_TEST:
                if (testFragment == null) {
                    testFragment = new TestFragment();
                    testFragment.setFrom(from);
                    mFragmentTransaction.add(R.id.frame_layout, testFragment);
                } else {
                    mFragmentTransaction.show(testFragment);
                }
                break;
            case SHOW_HOME:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    homeFragment.setFrom(from);
                    homeFragment.setOnUpdateProfileListener(this);
                    mFragmentTransaction.add(R.id.frame_layout, homeFragment);
                } else {
                    mFragmentTransaction.show(homeFragment);
                }
                break;
            case SHOW_ME:
                if (meFragment == null) {
                    meFragment = new MeFragment();
                    mFragmentTransaction.add(R.id.frame_layout, meFragment);
                } else {
                    mFragmentTransaction.show(meFragment);
                }
                break;
        }
        mFragmentTransaction.commit();
    }

    /**
     * 用来隐藏fragment的方法
     *
     * @param fragmentTransaction
     */
    private void hideFragment(FragmentTransaction fragmentTransaction) {
        //如果此fragment不为空的话就隐藏起来
        if (testFragment != null) {
            fragmentTransaction.hide(testFragment);
        }
        if (homeFragment != null) {
            fragmentTransaction.hide(homeFragment);
        }
        if (meFragment != null) {
            fragmentTransaction.hide(meFragment);
        }
    }

    @Override
    public void onBackPressed() {
        close();
    }

    private void close() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    private void sendProfileHttpTask() {
        if (TextUtils.isEmpty(GlobalApplication.getApplication().getSID())) {
            return;
        }
        if (!isSendProfileHttpTask && ToolUtils.isCanClick(3 * 1000)) {
            isSendProfileHttpTask = true;
            api_profile();
        }
    }

    /**
     * 获取用户概要
     */
    private void api_profile() {
//        AppMain.getInstance().onProfile(this, new AccountSdkCallBack() {
//            @Override
//            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
//                ProfileRsp response = ProfileRsp.decodeJson(ProfileRsp.class, jsonObject);
//                isSendProfileHttpTask = false;
//                if (response != null && TextUtils.equals(code, AccountSdk.LOCAL_PAY_SUCCESS)) {
//                    //更新用户概要数据
//                    GlobalApplication.getApplication().updateProfile(response);
//                    AccountSchema accountSchema = response.getAccountSchema();
//                    String balance = "";
//                    if (accountSchema != null) {
//                        balance = AmountUtil.getFormatAmount(ConfigCountry.KEY_CURRENCY_SYMBOL, accountSchema.balance);
//                    }
//                    boolean isHasMsg = response.isHasMsg();
//                    if (homeFragment != null) {
//                        homeFragment.updateProfile(balance, isHasMsg);
//                    }
//                    if (meFragment != null) {
//                        meFragment.onRefreshData();
//                    }
//
//                } else {
//                    LogUtil.d("TAG", "用户概要信息出错：" + errorMsg);
//                }
//            }
//        });
    }

    /**
     * 自动登录成功之后, 获取用户概要
     */
    @Override
    public void onUpdateProfile() {
        api_profile();
    }

    public FragmentManager getMFragmentManager() {
        return mFragmentManager;
    }

    @Override
    public void doAnything(int requestCode) {
        switch(requestCode){
            case request_type_lbs:
                LogUtil.d("","===========doAnything=====testFragment===");
                testFragment.getCityName();
                break;
        }
    }


}
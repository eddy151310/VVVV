package com.ahdi.wallet.app.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahdi.lib.utils.base.BaseFragment;
import com.ahdi.lib.utils.config.ConfigSP;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.LBSInstance;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.widgets.banner.Banner;
import com.ahdi.lib.utils.widgets.banner.BannerAdapter;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.callback.OtherSdkCallBack;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.response.BannerInfoRsp;
import com.ahdi.wallet.app.schemas.BannerSchema;
import com.ahdi.wallet.app.ui.activities.AppMainActivity;
import com.ahdi.wallet.app.ui.activities.poi.PoiSearchActivity;
import com.ahdi.wallet.app.ui.fragments.home.adapter.ViewPageAdapter;
import com.ahdi.wallet.app.ui.fragments.home.fragments.Frg1;
import com.ahdi.wallet.app.ui.fragments.home.fragments.Frg2;
import com.ahdi.wallet.app.ui.fragments.home.fragments.Frg3;
import com.ahdi.wallet.app.ui.fragments.home.fragments.Frg4;
import com.ahdi.wallet.app.ui.fragments.home.fragments.Frg5;
import com.ahdi.wallet.app.ui.fragments.home.utils.Message1;
import com.baidu.location.BDLocation;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Date: 2017/12/27 上午11:14
 * Author: kay lau
 * Description:
 */
public class TestFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = TestFragment.class.getSimpleName();
    /**
     * title
     */
    private Button btn_message = null;
    private Button btn_back = null;
    /**
     * banner
     */
    private Banner home_banner;

    /**
     * isAgainQuery: 锁屏与首页界面来回切换时, onResume()方法多次执行,
     * 保证一次网络请求执行完成之后, 如果界面再切换时继续执行下一次网络请求
     */
    private boolean isAgainQuery = true;

    /**
     * banner需要的数据
     */
    private List<BannerSchema> mDatas = new ArrayList<>();


    public static final int FROM_REQUEST = 1100;
    public static final String KEY_PUT_EXTRA_POI = "POI";

    SlidingTabLayout tablayout;
    ViewPager viewpager;
    SmartRefreshLayout smartrefresh;
    List<Fragment> fragmentList = new ArrayList<>();
    String[] strings = {"精选", "电器", "生活", "时尚", "超市"};
    ViewPageAdapter viewPageAdapter;
    int page = 0;//自己定义的参数，来监听Fragment的变化


    @Override
    protected View initRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.layout_fragment_test, container, false);
    }

    public void setFrom(String from) {
    }

    @Override
    protected void initView(View view) {

        View titleView = view.findViewById(R.id.title);
        titleView.setBackgroundResource(R.color.CC_00000000);
        //初始化控件
        btn_back = view.findViewById(R.id.btn_back);
        btn_back.setBackgroundColor(Color.TRANSPARENT);
        btn_back.setOnClickListener(this);
        btn_back.setTextColor(getResources().getColor(R.color.CC_282934));
        btn_back.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size_17));
//        btn_back.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.common_mbm_nav_logo), null, null, null);
//        btn_back.setCompoundDrawablePadding(5);
        //设置距左R.dimen.dp_10
        RelativeLayout.LayoutParams layoutParams;
        layoutParams = (RelativeLayout.LayoutParams) btn_back.getLayoutParams();
        layoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.dp_10), 0, 0, 0);//4个参数按顺序分别是左上右下
        btn_back.setLayoutParams(layoutParams);
        btn_message = view.findViewById(R.id.btn_next);
        btn_message.setVisibility(View.VISIBLE);
        btn_message.setOnClickListener(this);



        //banner
        home_banner = view.findViewById(R.id.home_banner);


        viewpager = view.findViewById(R.id.viewpager);
        tablayout = view.findViewById(R.id.tablayout);
        smartrefresh = view.findViewById(R.id.smartrefresh);
        //添加Fragment数据
        fragmentList.add(new Frg1());
        fragmentList.add(new Frg2());
        fragmentList.add(new Frg3());
        fragmentList.add(new Frg4());
        fragmentList.add(new Frg5());
        AppMainActivity mAppMainActivity = (AppMainActivity) mActivity;
        viewPageAdapter = new ViewPageAdapter(mAppMainActivity.getSupportFragmentManager());
        for (int i = 0; i < fragmentList.size(); i++) {
            viewPageAdapter.addtitleAndFrg(strings[i], fragmentList.get(i));
        }
        viewpager.setAdapter(viewPageAdapter);
        tablayout.setViewPager(viewpager);
        //监听ViewPager变化，左右滑动时，自己定义的page来监听现在展示的是哪个Fragment
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                page = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //监听tablayout变化，点击tablayout时，自己定义的page来监听现在展示的是哪个Fragment
        tablayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                page = position;
            }

            @Override
            public void onTabReselect(int position) {

            }
        });


        //刷新成功,项目根据自己的需求发不同的广播，让Fragment中接收做不同的事
        smartrefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                smartrefresh.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (page) {
                            case 0:
                                EventBus.getDefault().post(new Message1("frg1_down"));
                                break;
                            case 1:
                                EventBus.getDefault().post(new Message1("frg2_down"));
                                break;
                            case 2:
                                EventBus.getDefault().post(new Message1("frg3_down"));
                                break;
                            case 3:
                                EventBus.getDefault().post(new Message1("frg4_down"));
                                break;
                            case 4:
                                EventBus.getDefault().post(new Message1("frg5_down"));
                                break;
                        }
//                        Toast.makeText(MainActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                        smartrefresh.finishRefresh();
                    }
                }, 2000);
            }
        });



    }


    @Override
    protected void initData(View view) {

        //获取最新的定位缓存
        String cityName = AppGlobalUtil.getInstance().getString(mActivity, ConfigSP.SP_KEY_STREET_NAME);
        if(TextUtils.isEmpty(cityName)){
            AppMainActivity mainActivity = (AppMainActivity) this.mActivity;
            if (!mainActivity.checkPermissions(mainActivity.request_type_lbs)) {
                mainActivity.requestPermissions(mainActivity.request_type_lbs);
            } else {
                getCityName();
            }
        }else{
            btn_back.setText(cityName);
        }

        refreshBalance();
        updateMsgStatus(ProfileUserUtil.getInstance().isHasMsg());
        initBanner();
        try {
            refreshBanner(new JSONObject(AppGlobalUtil.getInstance().getString(mActivity, Constants.LOCAL_BANNER_JSON_KEY)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_back:
                Intent intent = new Intent(mActivity, PoiSearchActivity.class);
                intent.putExtra(KEY_PUT_EXTRA_POI , btn_back.getText().toString());
                startActivityForResult(intent ,FROM_REQUEST);
                break;
        }
    }




    @Override
    public void onResume() {
        super.onResume();
        if (isHidden()) {
            LogUtil.d(TAG, " ----------- isHidden------------------");
            return;
        }
        refreshBalance();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onResume();
        }
    }

    private void refreshBalance() {
        String balance = ProfileUserUtil.getInstance().getAccountBalance();
        if (TextUtils.isEmpty(balance)) {
            balance = "0";
        }
    }


    private void updateMsgStatus(boolean isHasMsg) {
        if (isHasMsg) {
            btn_message.setBackgroundResource(R.drawable.selector_btn_has_message);
        } else {
            btn_message.setBackgroundResource(R.drawable.selector_btn_no_message);
        }
    }


    /**
     * 获取banner的数据
     */
    private void getBannerInfo() {
        if (!isAgainQuery) {
            return;
        }
        isAgainQuery = false;
        AppMain.getInstance().getBannerInfo(mActivity, Constants.BANNER_REQUEST_PARAM, new OtherSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                isAgainQuery = true;
                if (TextUtils.equals(code, Constants.RET_CODE_SUCCESS)) {
                    refreshBanner(jsonObject);
                } else {
                    LogUtil.e(TAG, errorMsg);
                }
            }
        });
    }

    public void refreshBanner(JSONObject jsonObject) {
        if (jsonObject == null || mActivity == null) {
            return;
        }
        BannerInfoRsp bannerInfoResponse = BannerInfoRsp.decodeJson(BannerInfoRsp.class, jsonObject);
        if (bannerInfoResponse != null) {
            //保存相关数据
            AppGlobalUtil.getInstance().putString(AppGlobalUtil.getInstance().getContext(), Constants.LOCAL_BANNER_JSON_KEY, jsonObject.toString());

            //轮播Banner
            BannerSchema[] scrollBanners = bannerInfoResponse.scrollBanners;
            if (scrollBanners != null && scrollBanners.length > 0) {
                ArrayList<BannerSchema> mDatasTemp = new ArrayList<>(Arrays.asList(scrollBanners));

                //1、长度不同 直接刷新
                if (mDatas.size() != mDatasTemp.size()) {
                    updateBannerDatas(mDatasTemp);
                    return;
                }

                //2、循环变遍历
                for (int j = 0; j < mDatas.size(); j++) {
                    String urlLocal = mDatas.get(j).getContent();
                    String urlTemp = mDatasTemp.get(j).getContent();

                    //2.1  两个数组中的图片click地址 更新
                    mDatas.get(j).setClick(mDatasTemp.get(j).getClick());

                    //2.2 两个数组中的图片地址 不相等  直接刷新
                    if (!TextUtils.equals(urlLocal, urlTemp)) {
                        updateBannerDatas(mDatasTemp);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 更新轮播banner的数据
     *
     * @param mDatasTemp
     */
    private void updateBannerDatas(ArrayList<BannerSchema> mDatasTemp) {
        mDatas.clear();
        mDatas.addAll(mDatasTemp);
        home_banner.notifyDataHasChanged();
    }

    private void initBanner() {
        mDatas.add(new BannerSchema());//占位
        home_banner.setBannerAdapter(new BannerAdapter<BannerSchema>(mDatas) {

            @Override
            protected void bindTips(TextView tv, BannerSchema bannerModel) {
            }

            @Override
            public void bindImage(ImageView imageView, BannerSchema bannerModel) {
                ImageDownUtil.downScrollBannerImage(mActivity, bannerModel.getContent(), imageView);
            }
        });

        home_banner.setOnBannerItemClickListener(new Banner.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }
        });
        home_banner.notifyDataHasChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (home_banner != null) {
            home_banner.pauseScroll();
            home_banner.removeAllViews();
            home_banner = null;
        }
    }


    public void getCityName() {
        LBSInstance.getInstance().getLBS(new LBSInstance.LBSCallBack() {
            @Override
            public void onSuccess(BDLocation location) {
                LogUtil.d(TAG, "getStreet  ===: " + location.getStreet()  );
                LogUtil.d(TAG, "lat  ===: " + location.getLatitude() );
                LogUtil.d(TAG, "lng  ===: " + location.getLongitude() );

                if(!TextUtils.isEmpty(location.getStreet() )){
                    AppGlobalUtil.getInstance().putString(mActivity , ConfigSP.SP_KEY_STREET_NAME, location.getStreet() );
                }

                btn_back.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btn_back.setText(location.getStreet());
                    }
                },0);

            }
            @Override
            public void onFail(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "errorCode  ===: " + errorCode );
                LogUtil.d(TAG, "errorMsg  ===: " + errorMsg );
            }
        });
    }

    public void updataStreet(String street){
        btn_back.setText(street);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d("","===========requestCode=====requestCode===:" + requestCode);
        LogUtil.d("","===========resultCode=====resultCode===:" + resultCode);
        if(requestCode == FROM_REQUEST && resultCode == FROM_REQUEST ) {
            updataStreet(data.getExtras().getString(TestFragment.KEY_PUT_EXTRA_POI));
        }
    }
}
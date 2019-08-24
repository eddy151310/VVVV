package com.ahdi.wallet.app.ui.aaaa;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.ahdi.lib.utils.base.GlobalContextWrapper;
import com.ahdi.lib.utils.utils.LanguageUtil;
import com.ahdi.lib.utils.utils.PermissionsActivity;
import com.ahdi.lib.utils.utils.ScreenAdaptionUtil;
import com.ahdi.lib.utils.utils.StatusBarUtil;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.ui.aaaa.ccc.MyOpenPagerAdapter;
import com.ahdi.wallet.app.ui.aaaa.ccc.SessionEntity;
import com.ahdi.wallet.app.ui.aaaa.ccc.SimpleViewPagerIndicator;
import com.ahdi.wallet.app.ui.aaaa.ddd.DDDViewPager;

import java.util.ArrayList;
import java.util.List;

public class ServiceDetailsActivity2 extends PermissionsActivity implements View.OnClickListener {

    private static final String TAG = ServiceDetailsActivity2.class.getSimpleName();
    private String[] mTitles = new String[] { "简介", "评价", "相关" };


    DDDViewPager mViewPager;
    private MyOpenPagerAdapter openPagerAdapter;

    // 固定标题和滑动标题 LinearLayout
    private LinearLayout ll_title_in_scrollview;
    private SimpleViewPagerIndicator mIndicator_in_scrollview ;


    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenAdaptionUtil.setCustomDensity(this, getApplication());
        setContentView(R.layout.aaaa_servicede_layout);
        getWindow().setBackgroundDrawableResource(R.color.CC_F1F2F6); //代码设置bg 防止键盘弹起 闪黑屏
        StatusBarUtil.setStatusBar(this);
        initView();
    }


    private void initView() {
        ll_title_in_scrollview = findViewById(R.id.ll_title_in_scrollview);
        mIndicator_in_scrollview = ll_title_in_scrollview.findViewById(R.id.id_stickynavlayout_indicator);
        mIndicator_in_scrollview.setTitles(mTitles);
        mIndicator_in_scrollview.setOnPageChangeListener(new SimpleViewPagerIndicator.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mViewPager.setCurrentItem(position);
            }
        });



        refreshLayout = findViewById(R.id.fresh_layout);
        //设置下拉刷新时的操作
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //具体操作
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(true);
            }
        });

        setVp2();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(GlobalContextWrapper.wrap(newBase, LanguageUtil.getLanguage(newBase)));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
    }


    @Override
    public void onBackPressed() {
        close();
    }

    private void close() {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                close();
                return true;
            default:
                return false;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void setVp2( ) {
        mViewPager = findViewById(R.id.vp);
        mViewPager.setNestedpParent((ViewGroup) mViewPager.getParent());

        List<SessionEntity> datas = new ArrayList<>();
        for(int i = 0 ; i < mTitles.length ; i++){
            SessionEntity sessionEntity = new SessionEntity();
            sessionEntity.name = "AAAA== " +   i + " ==AAAA :";
            sessionEntity.isSelected = true;
            datas.add(sessionEntity);
        }

//        SessionEntity sessionEntity = new SessionEntity();
//        sessionEntity.name = "AAAA== " +    " ==AAAA :";
//        sessionEntity.isSelected = true;
//        datas.add(sessionEntity);

        openPagerAdapter = new MyOpenPagerAdapter(this , getSupportFragmentManager(), datas);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mIndicator_in_scrollview.scroll(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setOffscreenPageLimit(datas.size());
        mViewPager.setAdapter(openPagerAdapter);
        mViewPager.setCurrentItem(0);
    }

}

package com.ahdi.wallet.app.ui.activities.voucher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.ahdi.wallet.app.utils.ConstantsPayment;
import com.ahdi.lib.utils.base.AppBaseActivity;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.CustomViewPager;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.ui.adapters.ViewPagerAdapter;
import com.ahdi.wallet.app.ui.fragments.VoucherFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaohe
 * 显示优惠券列表的页面
 */
public class VoucherListActivity extends AppBaseActivity {
    private static final String TAG = "VoucherListActivity";

    private TextView tv_valid,tv_history;
    private CustomViewPager viewPager;
    private List<Fragment> fragments = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        setContentView(R.layout.activity_voucher_list);
        initCommonTitle(getString(R.string.VoucherList_A0));
        initView();
        initData();
    }

    public void initView() {
        tv_valid = findViewById(R.id.tv_valid);
        tv_history = findViewById(R.id.tv_history);
        tv_valid.setOnClickListener(this);
        tv_history.setOnClickListener(this);

        viewPager = findViewById(R.id.voucher_view_pager);
    }

    private void initData() {
        fragments = new ArrayList<Fragment>();

        VoucherFragment validVoucherFragment = new VoucherFragment();
        Bundle validBundle = new Bundle();
        validBundle.putInt(ConstantsPayment.KEY_VOUCHER_TYPE, ConstantsPayment.VOUCHER_TYPE_VALID);
        validVoucherFragment.setArguments(validBundle);
        fragments.add(validVoucherFragment);

        VoucherFragment historyVoucherFragment = new VoucherFragment();
        Bundle historyBundle = new Bundle();
        historyBundle.putInt(ConstantsPayment.KEY_VOUCHER_TYPE, ConstantsPayment.VOUCHER_TYPE_HISTORY);
        historyVoucherFragment.setArguments(historyBundle);
        fragments.add(historyVoucherFragment);

        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragments));
        viewPager.setCanScroll(false);//禁止手指滑动
        showValidFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showValidFragment();
        if (fragments != null){
            ((VoucherFragment)fragments.get(0)).onRefreshList();
        }
        LogUtil.d(TAG, "执行 onNewIntent");
    }

    @Override
    public void onClick(View view) {
        if (!isCanClick()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.tv_valid) {
            showValidFragment();
        }else if (id == R.id.tv_history){
            showHistoryFragment();
        }
    }

    /**
     * 展示可用的列表
     */
    private void showValidFragment(){
        tv_valid.setSelected(true);
        tv_history.setSelected(false);
        viewPager.setCurrentItem(0, true);
        LogUtil.d(TAG, "显示可用优惠券列表");
    }

    /**
     * 展示历史记录列表
     */
    private void showHistoryFragment(){
        tv_valid.setSelected(false);
        tv_history.setSelected(true);
        viewPager.setCurrentItem(1, true);
        LogUtil.d(TAG, "显示历史优惠券列表");
    }
}
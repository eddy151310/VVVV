package com.ahdi.wallet.app.ui.fragments.home.fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import com.ahdi.lib.utils.R;

import com.ahdi.wallet.app.ui.fragments.home.adapter.ReclAdapter;
import com.ahdi.wallet.app.ui.fragments.home.bean.ShoppingBean;
import com.ahdi.wallet.app.ui.fragments.home.interfaces.OnItem;
import com.ahdi.wallet.app.ui.fragments.home.utils.Message1;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class Frg1 extends BaseLoadFragment {
    ReclAdapter reclAdapter;
    RecyclerView recyclerView;
    List<ShoppingBean> shoppingBeanList = new ArrayList<>();
    Integer[] integers = {R.mipmap.bankcard_add_card
            , R.mipmap.bankcard_add_card
            , R.mipmap.bankcard_add_card
            , R.mipmap.bankcard_add_card
            , R.mipmap.bankcard_add_card
            , R.mipmap.bankcard_add_card };
    int p = 0;
    SmartRefreshLayout smartrefresh;
    @Override
    protected int setContentView() {
        return R.layout.frg_1;
    }

    @Override
    protected void init() {
        recyclerView = rootView.findViewById(R.id.recl);
        smartrefresh = rootView.findViewById(R.id.smartrefresh);
        reclAdapter = new ReclAdapter(getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setAdapter(reclAdapter);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        shoppingBeanList.clear();
        p = 0;
        for (int i = 0; i < integers.length; i++) {
            p++;
            ShoppingBean shoppingBean = new ShoppingBean();
            shoppingBean.setPage(p);
            shoppingBean.setPic(integers[i]);
            shoppingBeanList.add(shoppingBean);
        }
        reclAdapter.addData(shoppingBeanList);
        reclAdapter.notifyDataSetChanged();


        reclAdapter.onItemClick(new OnItem() {
            @Override
            public void onItem(int position) {
                Toast.makeText(getContext(), "当前点击的是" + (position + 1) + "个", Toast.LENGTH_SHORT).show();
            }
        });
        smartrefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                smartrefresh.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < integers.length; i++) {
                            p++;
                            ShoppingBean shoppingBean = new ShoppingBean();
                            shoppingBean.setPage(p);
                            shoppingBean.setPic(integers[i]);
                            shoppingBeanList.add(shoppingBean);
                        }
                        reclAdapter.addData(shoppingBeanList);
                        reclAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "加载更多成功", Toast.LENGTH_SHORT).show();
                        smartrefresh.finishLoadMore();
                    }
                }, 2000);

            }
        });
    }

    @Override
    protected void lazyLoad() {
        //第一个Fragment不用做延迟加载处理
    }

    //接收广播处理不同的事务
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(Message1 message) {
        String str = message.getStr();
        switch (str) {
            case "frg1_down":
                shoppingBeanList.clear();
                p = 0;
                for (int i = 0; i < integers.length; i++) {
                    p++;
                    ShoppingBean shoppingBean = new ShoppingBean();
                    shoppingBean.setPage(p);
                    shoppingBean.setPic(integers[i]);
                    shoppingBeanList.add(shoppingBean);
                }
                reclAdapter.addData(shoppingBeanList);
                reclAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);

    }


}

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
public class Frg5 extends BaseLoadFragment {
    ReclAdapter reclAdapter;
    RecyclerView recl;
    List<ShoppingBean> shoppingBeanList=new ArrayList<>();
    Integer[] integers = {R.mipmap.app_icon, R.mipmap.app_icon, R.mipmap.app_icon, R.mipmap.app_icon, R.mipmap.app_icon, R.mipmap.app_icon};
    int p=0;
    SmartRefreshLayout smartrefresh;
    @Override
    protected int setContentView() {
        return R.layout.frg_5;
    }

    @Override
    protected void init() {
        recl=rootView.findViewById(R.id.recl);
        smartrefresh=rootView.findViewById(R.id.smartrefresh);
        reclAdapter=new ReclAdapter(getContext());
        recl.setLayoutManager(new GridLayoutManager(getContext(),2));
        recl.setAdapter(reclAdapter);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        reclAdapter.onItemClick(new OnItem() {
            @Override
            public void onItem(int position) {
                Toast.makeText(getContext(), "当前点击的是"+(position+1)+"个", Toast.LENGTH_SHORT).show();
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
        shoppingBeanList.clear();
        p=0;
        for (int i=0;i<integers.length;i++){
            p++;
            ShoppingBean shoppingBean=new ShoppingBean();
            shoppingBean.setPage(p);
            shoppingBean.setPic(integers[i]);
            shoppingBeanList.add(shoppingBean);
        }
        reclAdapter.addData(shoppingBeanList);
        reclAdapter.notifyDataSetChanged();
    }

    //接收广播处理不同的事务
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(Message1 message) {
        String str = message.getStr();
        switch (str) {
            case "frg5_down":
                shoppingBeanList.clear();
                p=0;
                for (int i=0;i<integers.length;i++){
                    p++;
                    ShoppingBean shoppingBean=new ShoppingBean();
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

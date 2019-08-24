package com.ahdi.wallet.app.ui.fragments.home.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

//ViewPage适配器
public class ViewPageAdapter extends FragmentStatePagerAdapter {
    List<Fragment> fragmentList=new ArrayList<>();
    List<String> stringList=new ArrayList<>();

    public ViewPageAdapter(FragmentManager fm) {
        super(fm);
    }
    public void addtitleAndFrg(String title, Fragment fragment){
        stringList.add(title);
        fragmentList.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return stringList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        //延迟加载Fragment，不进行销毁处理
    }
}

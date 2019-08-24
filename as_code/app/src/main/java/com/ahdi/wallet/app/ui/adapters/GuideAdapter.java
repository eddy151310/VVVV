package com.ahdi.wallet.app.ui.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * @author zhaohe
 * @date 2018/7/30.
 */

public class GuideAdapter extends PagerAdapter {


    private List<Drawable> imgDrawable;
    private Context context;

    public GuideAdapter(Context context, List<Drawable> imgDrawable) {
        this.context = context;
        this.imgDrawable = imgDrawable;
    }

    @Override
    public int getCount() {
        return imgDrawable.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        ImageView view  = new ImageView(context);
        view.setImageDrawable(imgDrawable.get(position));
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}

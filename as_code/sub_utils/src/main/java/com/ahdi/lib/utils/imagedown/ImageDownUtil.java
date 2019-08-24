package com.ahdi.lib.utils.imagedown;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ImageView;

import com.ahdi.lib.utils.R;
import com.ahdi.lib.utils.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

/**
 * @author admin
 * @date Created by xiaoniu on 2018/1/12.
 * @email zhao_zhaohe@163.com
 * <p>
 * 统一的下载util
 * 方便替换下载框架
 */

public class ImageDownUtil {

    private static final String TAG = "ImageDownUtil";

    /**
     * 下载自己的头像
     *
     * @param activity
     * @param userIconUrl
     * @param imageView
     */
    public static void downMySelfPhoto(Activity activity, String userIconUrl, ImageView imageView) {
        if (TextUtils.isEmpty(userIconUrl)) {
            LogUtil.d(TAG, "下载头像失败，userIconUrl 为空");
            return;
        }
        if (activity == null) {
            LogUtil.d(TAG, "下载头像失败，activity 为空");
            return;
        }
        if (imageView == null) {
            LogUtil.d(TAG, "下载头像失败，imageView 为空");
            return;
        }
        RequestOptions options = getRequestOptions(R.mipmap.me_img_user_avatar, R.mipmap.me_img_user_avatar);
        //圆头像
        options.optionalCircleCrop();
        Glide.with(activity).load(userIconUrl).apply(options).into(imageView);
    }

    /**
     * 下载其他用户头像，只使用内存缓存和网络图片
     *
     * @param activity
     * @param userIconUrl
     * @param imageView
     */
    public static void downOtherUserPhotoImage(Activity activity, String userIconUrl, ImageView imageView) {
        if (TextUtils.isEmpty(userIconUrl)) {
            LogUtil.d(TAG, "下载头像失败，userIconUrl 为空");
            return;
        }
        if (activity == null) {
            LogUtil.d(TAG, "下载头像失败，activity 为空");
            return;
        }
        if (imageView == null) {
            LogUtil.d(TAG, "下载头像失败，imageView 为空");
            return;
        }
        RequestOptions options = getRequestOptions(R.mipmap.me_img_user_avatar, R.mipmap.me_img_user_avatar);
        options.diskCacheStrategy(DiskCacheStrategy.NONE);
        //圆头像
        options.optionalCircleCrop();
        Glide.with(activity).load(userIconUrl).apply(options).into(imageView);
    }

    /**
     * 下载其他用户头像，只使用内存缓存和网络图片
     *
     * @param context
     * @param userIconUrl
     * @param imageView
     */
    public static void downOtherUserPhotoImage(Context context, String userIconUrl, ImageView imageView) {
        if (context == null) {
            LogUtil.d(TAG, "下载头像失败，context 为空");
            return;
        }
        if (imageView == null) {
            LogUtil.d(TAG, "下载头像失败，imageView 为空");
            return;
        }
        RequestOptions options = getRequestOptions(R.mipmap.me_img_user_avatar, R.mipmap.me_img_user_avatar);
        options.diskCacheStrategy(DiskCacheStrategy.NONE);
        //圆头像
        options.optionalCircleCrop();
        Glide.with(context).load(userIconUrl).apply(options).into(imageView);
    }

    /**
     * 下载voucher icn
     *
     * @param context
     * @param iconUrl
     * @param imageView
     */
    public static void downVoucherIcon(Context context, String iconUrl, ImageView imageView) {
        downImage(context, iconUrl, imageView, R.mipmap.voucher_icon_default, R.mipmap.voucher_icon_default, false);
    }

    /**
     * 下载银行卡icn
     *
     * @param context
     * @param iconUrl
     * @param imageView
     */
    public static void downBankIconImage(Context context, String iconUrl, ImageView imageView) {
        downImage(context, iconUrl, imageView, R.mipmap.common_pay_type_icon_default, R.mipmap.common_pay_type_icon_default, false);
    }

    /**
     * 下载滚动的banner图片
     *
     * @param context
     * @param iconUrl
     * @param imageView
     */
    public static void downScrollBannerImage(Context context, String iconUrl, ImageView imageView) {
        LogUtil.d(TAG, "开始下载轮播图片");
        downImage(context, iconUrl, imageView, R.mipmap.home_scroll_banner, R.mipmap.home_scroll_banner, true);
    }

    /**
     * 下载静态的banner图片
     *
     * @param context
     * @param iconUrl
     * @param imageView
     */
    public static void downStaticBannerImage(Context context, String iconUrl, ImageView imageView) {
        downImage(context, iconUrl, imageView, R.mipmap.home_bottom_banner, R.mipmap.home_bottom_banner, true);
    }

    /**
     * 下载图片
     *
     * @param context
     * @param iconUrl
     * @param imageView
     * @param placeholderIcon
     * @param errorIcon
     * @param cache           是否存本地
     */
    private static void downImage(Context context, String iconUrl, ImageView imageView, int placeholderIcon, int errorIcon, boolean cache) {
        if (context == null) {
            LogUtil.d(TAG, "下载图片失败，context 为空");
            return;
        }
        if (imageView == null) {
            LogUtil.d(TAG, "下载图片失败，imageView 为空");
            return;
        }
        RequestOptions options = getRequestOptions(placeholderIcon, errorIcon);
        if (!cache) {
            options.diskCacheStrategy(DiskCacheStrategy.NONE);
        }
        Glide.with(context).load(iconUrl).apply(options).into(imageView);
    }

    /**
     * @param lodeIcon 加载中占位图片
     * @param failIcon 加载失败图片
     * @return
     */
    @NonNull
    private static RequestOptions getRequestOptions(int lodeIcon, int failIcon) {
        return new RequestOptions()
                .placeholder(lodeIcon)  // 加载中占位图片
                .error(failIcon);       // 加载失败图片
    }

    /**
     * 下载充值类型icon
     *
     * @param context
     * @param iconUrl
     * @param imageView
     */
    public static void downTopupTypeIconImage(Context context, String iconUrl, ImageView imageView) {
        downImage(context, iconUrl, imageView, R.mipmap.top_up_bitmap, R.mipmap.top_up_bitmap, false);
    }

}

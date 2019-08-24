package com.ahdi.wallet.app.ui.activities.other;

import android.os.Bundle;

import com.ahdi.lib.utils.base.WebBaseActivity;
import com.ahdi.lib.utils.utils.LogUtil;

/***
 * @author zhaohe
 * 用于展示普通的网页（help 和agree等）
 */
public class WebBannerActivity extends WebBaseActivity {

    public static final String TAG = WebBannerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        LogUtil.e(TAG, "---onCreate()---");

    }

}
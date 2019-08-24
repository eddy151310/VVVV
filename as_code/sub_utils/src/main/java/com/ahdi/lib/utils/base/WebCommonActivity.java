package com.ahdi.lib.utils.base;

import android.os.Bundle;

import com.ahdi.lib.utils.utils.LogUtil;

/***
 * @author zhaohe
 * 用于展示普通的网页（help 和agree等）
 */
public class WebCommonActivity extends WebBaseActivity {

    private static final String TAG = WebCommonActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        LogUtil.e(TAG, "---WebCommonActivity---onCreate()---");
    }
}
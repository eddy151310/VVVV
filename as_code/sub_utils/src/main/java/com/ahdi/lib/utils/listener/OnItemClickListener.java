package com.ahdi.lib.utils.listener;

import android.view.View;

/**
 * 作者：lixue on 2017/10/26 15:16
 */

public  interface OnItemClickListener {
    /**
     * 点击事件回调
     * @param view
     * @param position
     */
    void onItemClick(View view , int position);
}

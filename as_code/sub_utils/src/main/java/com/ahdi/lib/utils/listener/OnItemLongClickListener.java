package com.ahdi.lib.utils.listener;

import android.view.View;

/**
 * @author zhaohe
 * item的长按事件
 */

public  interface OnItemLongClickListener {
    /**
     * 长按事件回调
     * @param view
     * @param position
     */
    void onItemLongClick(View view, int position);
}

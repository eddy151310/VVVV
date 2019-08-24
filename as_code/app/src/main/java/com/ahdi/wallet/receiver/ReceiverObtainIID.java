package com.ahdi.wallet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.app.main.AppMain;

/**
 * Date: 2017/10/17 下午3:02
 * Author: kay lau
 * Description:
 */
public class ReceiverObtainIID extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 终端设备和推送通知服务绑定
        String sid = GlobalApplication.getApplication().getSID();
        if (!TextUtils.isEmpty(sid)) {
            AppMain.getInstance().onTerminalBind();
            LogUtil.e("ReceiverObtainIID", "onTerminalBind()");
        }
    }
}

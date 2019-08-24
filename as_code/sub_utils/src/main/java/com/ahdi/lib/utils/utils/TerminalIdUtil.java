package com.ahdi.lib.utils.utils;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.lib.utils.cryptor.aes.MD5;

/**
 * @author admin
 *  设备终端唯一标识
 */
public class TerminalIdUtil {

    public static final String ANDROID_ID = "android_id";

    private static String getTerminalID(Context context) {
        return MD5.MD5Tool(DeviceUtil.getSN() + DeviceUtil.getAndroidId(context) + DeviceUtil.getMacAddress(), "UTF-8");
    }

    /**
     * 生成terminalid时优先从本地缓存文件获取，如果没有缓存则现有规则生成之后再做文件缓存
     */
    public static String getTerminalID() {
        if (!TextUtils.isEmpty(AppGlobalUtil.getInstance().getTid())) {
            return AppGlobalUtil.getInstance().getTid();
        }
        String terminlid = getTerminalID(AppGlobalUtil.getInstance().getContext());
        AppGlobalUtil.getInstance().setTid(terminlid);
        return terminlid;
    }

}

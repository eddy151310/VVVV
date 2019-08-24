package com.ahdi.lib.utils.paycode;

import android.text.TextUtils;

import com.ahdi.lib.utils.utils.LogUtil;

/**
 * Date: 2018/12/4 上午11:41
 * Author: kay lau
 * Description:
 */
public class PayCodeUtil {

    private static PayCodeUtil instance;
    private PayCodeCreater payCodeCreater;

    private PayCodeUtil() {
    }

    public static PayCodeUtil getInstance() {
        if (instance == null) {
            synchronized (PayCodeUtil.class) {
                if (instance == null) {
                    instance = new PayCodeUtil();
                }
            }
        }
        return instance;
    }


    public PayCodeBean getPayCode(String key, long clientLong, long timeDif, int position, int next) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        if (position < 0 || position > 9) {
            LogUtil.e("tag", "(position < 0 || position > 9)");
            return null;
        }
        payCodeCreater = new PayCodeCreater(clientLong, key.getBytes(), timeDif);
        payCodeCreater.setNext(next);
        return payCodeCreater.nextCode(position);
    }

    public PayCodeBean updatePayCode(String key, long clientLong, long timeDif, int position, int next) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        if (position < 0 || position > 9) {
            LogUtil.e("tag", "(position < 0 || position > 9)");
            return null;
        }
        if (payCodeCreater == null) {
            payCodeCreater = new PayCodeCreater(clientLong, key.getBytes(), timeDif);
        }
        payCodeCreater.setNext(next);
        return payCodeCreater.nextCode(position);
    }

}

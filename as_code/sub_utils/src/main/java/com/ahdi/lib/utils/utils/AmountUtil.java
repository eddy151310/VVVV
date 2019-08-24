package com.ahdi.lib.utils.utils;

import android.text.TextUtils;

import com.ahdi.lib.utils.config.ConfigCountry;

/**
 * @author xiaoniu
 * @date 2018/10/9.
 *
 * 金额工具类
 * 格式化，去除格式化等
 */

public class AmountUtil {

    private static final String TAG = "AmountUtil";

    /**
     * 去掉格式化
     *
     * @param unstr
     * @return
     */

    public static String unFormatString(String unstr) {
        if (TextUtils.isEmpty(unstr)) {
            return "";
        }
        return unstr.replace(ConfigCountry.AMOUNT_FORMAT_SYMBOL, "");
    }

    /**
     * 格式化金额
     *
     * @param str
     * @return
     */
    public static String formatAmount(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }

        if(str.length() <= ConfigCountry.AMOUNT_FORMAT_LENGTH ){
            return str;
        }

        str = new StringBuilder(str).reverse().toString(); // 先将字符串颠倒顺序
        String str2 = "";
        for (int i = 0; i < str.length(); i++) {
            if (i * ConfigCountry.AMOUNT_FORMAT_LENGTH + ConfigCountry.AMOUNT_FORMAT_LENGTH > str.length()) {
                str2 += str.substring(i * ConfigCountry.AMOUNT_FORMAT_LENGTH, str.length());
                break;
            }
            str2 += str.substring(i * ConfigCountry.AMOUNT_FORMAT_LENGTH, i * ConfigCountry.AMOUNT_FORMAT_LENGTH + ConfigCountry.AMOUNT_FORMAT_LENGTH) + ConfigCountry.AMOUNT_FORMAT_SYMBOL;
        }
        if (str2.endsWith(ConfigCountry.AMOUNT_FORMAT_SYMBOL)) {
            str2 = str2.substring(0, str2.length() - 1);
        }
        return new StringBuilder(str2).reverse().toString();
    }

    /**
     * 带负号的以及货币符号的格式化金额
     *
     * @param currencyUnit 货币符号
     * @param amount       金额
     * @param amtSign      金额正负
     * @return
     */
    public static String getFormatAmount(String currencyUnit, String amount, String amtSign) {
        if (TextUtils.isEmpty(amount)) {
            return "";
        }
        String str = amtSign.concat(currencyUnit);

        return str.concat(formatAmount(amount));
    }

    public static String getFormatAmount(String currencyUnit, String amount) {
        if (TextUtils.isEmpty(amount)) {
            return "";
        }
        return currencyUnit + formatAmount(amount);
    }

    /**
     * 检验金额是否合法
     *
     * @param formatAmount
     * @return true 合法  false 不合法
     */
    public static boolean checkAmount(String formatAmount) {
        if (TextUtils.isEmpty(formatAmount)) {
            return false;
        }
        String unFormatAmount = unFormatString(formatAmount);
        if (TextUtils.isEmpty(unFormatAmount)) {
            return false;
        }
        try {
            if (Long.parseLong(unFormatAmount) == 0) {
                return false;
            }
        } catch (Exception e) {
            LogUtil.d(TAG, "当前金额:" + formatAmount + " 转换异常：" + e.toString());
            return false;
        }

        return true;
    }

    public static long parseLong(String longStr) {
        long parse = 0;
        try {
            parse = Long.parseLong(longStr);
        } catch (NumberFormatException e) {
            LogUtil.e("tag", e.toString());
        }
        return parse;
    }
}

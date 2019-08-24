package com.ahdi.lib.utils.utils;

import android.content.Context;

import com.ahdi.lib.utils.R;
import com.ahdi.lib.utils.config.ConfigCountry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串服务
 *
 * @author admin
 */
public final class StringUtil {

    public static String getMatcher(String regex, String source) {
        String result = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            result = matcher.group(1);
        }
        return result;
    }

    public static String formatPhoneNumber(String loginName) {
        if (!loginName.startsWith(ConfigCountry.KEY_AREA_CODE)) {
            return ConfigCountry.KEY_AREA_CODE.concat(loginName);
        }
        return loginName;
    }

    //生成随机数字和字母,
    public static String getStringRandom(int length) {

        char[] ss = new char[length];
        int i = 0;
        while (i < length) {
            int f = (int) (Math.random() * 3);
            if (f == 0)
                ss[i] = (char) ('A' + Math.random() * 26);
            else if (f == 1)
                ss[i] = (char) ('a' + Math.random() * 26);
            else
                ss[i] = (char) ('0' + Math.random() * 10);
            i++;
        }
        String is = new String(ss);
        return is;
    }

    public static ArrayList<String> getGenderList(Context context) {
        ArrayList<String> list = new ArrayList<>();
        list.add(context.getString(R.string.General_F0));
        list.add(context.getString(R.string.General_G0));
        return list;
    }

    /**
     * 对字符串数组进行升序排序的方法
     *
     * @param str
     * @param separator
     * @return
     */
    public static String sortNumberString(String str, String separator) {
        String[] stringArray = str.split(separator);
        Arrays.sort(stringArray);
        StringBuilder returnString = new StringBuilder();
        for (int i = 0; i < stringArray.length; i++) {
            returnString.append(stringArray[i]);
            if (i != stringArray.length - 1)
                returnString.append(separator);
        }
        return returnString.toString();
    }

    /**
     * Http GET请求Url拼接
     *
     * @param url
     * @param params
     * @return
     */
    public static String buildHttpGetUrlParmas(String url, Map<String, String> params) {
        // 添加url参数
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            StringBuffer sb = null;
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if (sb == null) {
                    sb = new StringBuffer();
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
            url += sb.toString();
        }
        return url;
    }

}

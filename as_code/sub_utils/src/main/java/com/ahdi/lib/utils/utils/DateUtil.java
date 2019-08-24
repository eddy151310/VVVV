package com.ahdi.lib.utils.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author xiaoniu
 * @date 2018/10/9.
 *
 * 处理日期格式等
 */

public class DateUtil {

    private static final String TAG = "DateUtil";
    /**
     * 时间戳转为 25-12-2017
     * 日月年
     *
     * @param time
     * @return
     */
    public static String formatTimeDMYNoHMS(long time) {
        return new SimpleDateFormat("dd-MM-yyyy").format(new Date(time));
    }

    /**
     * 根据时区 时间戳转为 25-12-2017
     * 日月年
     *
     * @param time
     * @param id  "GMT-8:00"...
     * @return
     */
    public static String formatTimeMDY(long time, String id) {
        TimeZone timeZone = TextUtils.isEmpty(id)? TimeZone.getDefault():TimeZone.getTimeZone(id);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 时间戳转为 02-29-2017 12:12
     * 日月年 时分   不要秒数
     *
     * @param time
     * @return
     */
    public static String formatTimeDMYHM(long time, TimeZone timeZone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        if (timeZone == null) {
            return dateFormat.format(new Date(time));
        }
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(new Date(time));
    }

    /**
     * 时间戳转为 2018-05-26 12:12
     * 年月日时分
     *
     * @param time
     * @return
     */
    public static String formatTimeYMDHM(long time, TimeZone timeZone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (timeZone == null) {
            return dateFormat.format(new Date(time));
        }
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(new Date(time));
    }

    /**
     * 时间戳转为 2017-12-12 12:12
     * 年月日时分
     *
     * @param time
     * @return
     */
    public static String formatTimeYMDHM(Date time, TimeZone timeZone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (timeZone != null) {
            dateFormat.setTimeZone(timeZone);
        }
        return dateFormat.format(time);
    }

    /**
     * 时间戳转为 12-2017
     * 月年
     *
     * @param time
     * @return
     */
    public static String formatTimeNoDHMS(long time, TimeZone timeZone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
        if (timeZone == null) {
            return dateFormat.format(new Date(time));
        }
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(new Date(time));
    }

    /**
     * 格式yyyyMMdd转为dd-MM-yyyy
     * @param yyyyMMdd
     * @return
     */
    public static String yyyyMMdd2ddMMyyyy(String yyyyMMdd){
        String formatTime = "";
        if (TextUtils.isEmpty(yyyyMMdd)){
            return formatTime;
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            return formatTimeDMYNoHMS(dateFormat.parse(yyyyMMdd).getTime());
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.d(TAG, "格式yyyyMMdd转为dd-MM-yyyy 出错");
        }
        return formatTime;
    }

    /**
     * 28-02-2019 --> 2019-02-28 12:01
     * @param ddMMyyyy
     * @return
     */
    public static String ddMMyyyy2yyyyMMddHHmm(String ddMMyyyy){
        try {
            return formatTimeYMDHM(new SimpleDateFormat("dd-MM-yyyy").parse(ddMMyyyy).getTime(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getTZOffset(TimeZone timeZone) {

        if (timeZone == null) {
            return TimeZone.getDefault().getRawOffset() / 3600 / 1000;
        }
        return timeZone.getRawOffset() / 3600 / 1000;
    }
}

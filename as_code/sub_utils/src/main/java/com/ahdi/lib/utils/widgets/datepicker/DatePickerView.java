package com.ahdi.lib.utils.widgets.datepicker;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ahdi.lib.utils.R;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.utils.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

public class DatePickerView {

    private static final String TAG = "DatePickerView";
    /**
     * 时间转换格式
     */
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm";

    /**
     * 定义结果回调接口
     */
    public interface ResultHandler {
        void handle(long time);
    }

    private ResultHandler handler;
    private Context context;
    private boolean canAccess = false;
    /**
     * 是否显示日
     */
    private boolean isShowDay;

    private static final int MAX_MONTH = 12;
    private Dialog datePickerDialog;
    private WheelView year_pv, month_pv, day_pv;

    /**
     * 月份对应的英文文案
     */
    private ArrayList<String> englishMonthList = new ArrayList<>(12);

    private ArrayList<String> year, month, day;
    private int startYear, startMonth, startDay, endYear, endMonth, endDay;
    private boolean spanYear, spanMon, spanDay;
    private Calendar selectedCalender, startCalendar, endCalendar;
    private TextView tv_select, tv_cancel;

    private String lastSelectedYear = "";
    private String lastSelectedMonth = "";
    private String lastSelectedDay = "";
    /**
     * 时区
     */
    private TimeZone timeZone = null;


    public DatePickerView(Context context, ResultHandler resultHandler, String startDate, String endDate) {
        LogUtil.d(TAG, startDate + "   " + endDate);
        englishMonthList.addAll(Arrays.asList(context.getResources().getStringArray(R.array.DialogMonths)));
        if (isValidDate(startDate, TIME_FORMAT) && isValidDate(endDate, TIME_FORMAT)) {
            canAccess = true;
            this.context = context;
            this.handler = resultHandler;
            selectedCalender = Calendar.getInstance();
            startCalendar = Calendar.getInstance();
            endCalendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
            if (timeZone != null) {
                sdf.setTimeZone(timeZone);
            }
            try {
                startCalendar.setTime(sdf.parse(startDate));
                endCalendar.setTime(sdf.parse(endDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            initDialog();
            initView();
        }
    }

    private void initDialog() {
        if (datePickerDialog == null) {
            datePickerDialog = new Dialog(context, R.style.ActionSheetDialogStyle);
            datePickerDialog.setCancelable(true);
            datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            datePickerDialog.setContentView(R.layout.layout_date_picker);
            Window window = datePickerDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = dm.widthPixels;
            window.setAttributes(lp);
        }
    }

    private void initView() {
        year_pv = (WheelView) datePickerDialog.findViewById(R.id.year_pv);
        month_pv = (WheelView) datePickerDialog.findViewById(R.id.month_pv);
        day_pv = (WheelView) datePickerDialog.findViewById(R.id.day_pv);
        tv_cancel = (TextView) datePickerDialog.findViewById(R.id.tv_cancel);
        tv_select = (TextView) datePickerDialog.findViewById(R.id.tv_select);
        if (isShowDay) {
            day_pv.setVisibility(View.VISIBLE);
        } else {
            day_pv.setVisibility(View.GONE);
        }
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ToolUtils.isCanClick()) {
                    return;
                }
                datePickerDialog.dismiss();
            }
        });

        tv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ToolUtils.isCanClick()) {
                    return;
                }
                //控件停止滚动时点击才有效
                if (year_pv.isStopScroll() && month_pv.isStopScroll() && day_pv.isStopScroll()){
                    handler.handle(selectedCalender.getTime().getTime());
                    datePickerDialog.dismiss();
                }
            }
        });
    }

    private void initParameter() {
        startYear = startCalendar.get(Calendar.YEAR);
        startMonth = startCalendar.get(Calendar.MONTH) + 1;
        startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        endYear = endCalendar.get(Calendar.YEAR);
        endMonth = endCalendar.get(Calendar.MONTH) + 1;
        endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        spanYear = startYear != endYear;
        spanMon = (!spanYear) && (startMonth != endMonth);
        spanDay = (!spanMon) && (startDay != endDay);
//        selectedCalender.setTime(startCalendar.getTime());
    }

    private void initTimer() {
        initArrayList();
        if (spanYear) {
            for (int i = startYear; i <= endYear; i++) {
                year.add(String.valueOf(i));
            }
            for (int i = startMonth; i <= MAX_MONTH; i++) {
                month.add(formatMonth(i));
            }
            for (int i = startDay; i <= startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(formatDay(i));
            }
        } else if (spanMon) {
            year.add(String.valueOf(startYear));
            for (int i = startMonth; i <= endMonth; i++) {
                month.add(formatMonth(i));
            }
            for (int i = startDay; i <= startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(formatDay(i));
            }
        } else if (spanDay) {
            year.add(String.valueOf(startYear));
            month.add(formatMonth(startMonth));
            for (int i = startDay; i <= endDay; i++) {
                day.add(formatDay(i));
            }
        } else {
            year.add(String.valueOf(startYear));
            month.add(formatMonth(startMonth));
            day.add(formatDay(startDay));
        }
        loadComponent();
    }

    /**
     * 将“0-9”转换为“00-09”
     */
    private String formatDay(int unit) {
        return unit < 10 ? "0" + String.valueOf(unit) : String.valueOf(unit);
    }

    /**
     * 将“1-12”转换为英文月份
     */
    private String formatMonth(int unit) {
        if (unit > 12 || unit < 1) {
            return "";
        }
        return englishMonthList.get(unit - 1);
    }

    private void initArrayList() {
        if (year == null) {
            year = new ArrayList<>();
        }
        if (month == null) {
            month = new ArrayList<>();
        }
        if (day == null) {
            day = new ArrayList<>();
        }
        year.clear();
        month.clear();
        day.clear();
    }

    private void loadComponent() {
        year_pv.setItems(year, 0);
        month_pv.setItems(month, 0);
        day_pv.setItems(day, 0);
    }

    private void addListener() {
        year_pv.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                if (TextUtils.equals(lastSelectedYear, item)) {
                    return;
                }
                lastSelectedYear = item;
                selectedCalender.set(Calendar.YEAR, Integer.parseInt(year.get(selectedIndex)));
                monthChange();
            }
        });
        month_pv.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                if (TextUtils.equals(lastSelectedMonth, item)) {
                    return;
                }
                lastSelectedMonth = item;

                selectedCalender.set(Calendar.DAY_OF_MONTH, 1);
                selectedCalender.set(Calendar.MONTH, englishMonthList.indexOf(month.get(selectedIndex)));
                if (isShowDay) {
                    dayChange();
                }
            }
        });
        day_pv.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                lastSelectedDay = item;
                selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(item));
            }
        });
    }

    private void monthChange() {
        initMonth(null);
        month_pv.postDelayed(new Runnable() {
            @Override
            public void run() {
                dayChange();
            }
        }, 100);
    }

    private void dayChange() {
        initDay(null);
    }

    /**
     * 显示弹窗
     *
     * @param time     选中的时间
     * @param timeZone 时区
     */
    public void show(String time, TimeZone timeZone) {
        this.timeZone = timeZone;
        show(time);
    }

    /**
     * 显示弹窗
     *
     * @param time 选中的时间
     */
    public void show(String time) {
        if (canAccess) {
            if (isValidDate(time, "yyyy-MM-dd")) {
                if (startCalendar.getTime().getTime() < endCalendar.getTime().getTime()) {
                    canAccess = true;
                    initParameter();
                    initTimer();
                    addListener();
                    setSelectedTime(time);
                    datePickerDialog.show();
                }
            } else {
                canAccess = false;
            }
        }
    }

    /**
     * 设置日期控件是否显示日
     */
    public void showDay(boolean show) {
        if (canAccess) {
            this.isShowDay = show;
            if (show) {
                day_pv.setVisibility(View.VISIBLE);
            } else {
                day_pv.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置日期控件默认选中的时间
     */
    private void setSelectedTime(String time) {
        if (canAccess) {
            String[] str = time.split(" ");
            String[] dateStr = str[0].split("-");

            initYear(dateStr[0]);
            initMonth(formatMonth(Integer.parseInt(dateStr[1])));
            initDay(formatDay(Integer.parseInt(dateStr[2])));
        }
    }

    /**
     * 初始化年
     */
    private void initYear(String currentYear) {
        lastSelectedYear = currentYear;
        year_pv.setItems(year, year.indexOf(currentYear));
        selectedCalender.set(Calendar.YEAR, Integer.parseInt(currentYear));
    }

    /**
     * 初始化月份
     */
    private void initMonth(String selectedMonth) {
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        LogUtil.d(TAG, "滚轮之后初始化年份" + selectedYear);
        month.clear();
        if (selectedYear == startYear && selectedYear == endYear) {
            for (int i = startMonth; i <= endMonth; i++) {
                month.add(formatMonth(i));
            }
        } else if (selectedYear == startYear && selectedYear < endYear) {
            for (int i = startMonth; i <= MAX_MONTH; i++) {
                month.add(formatMonth(i));
            }
        } else if (selectedYear > startYear && selectedYear == endYear) {
            for (int i = 1; i <= endMonth; i++) {
                month.add(formatMonth(i));
            }
        } else {
            for (int i = 1; i <= MAX_MONTH; i++) {
                month.add(formatMonth(i));
            }
        }
        if (TextUtils.isEmpty(selectedMonth) || month.indexOf(selectedMonth) == -1) {
            if (month.indexOf(lastSelectedMonth) != -1){
                selectedMonth = lastSelectedMonth;
            }else {
                selectedMonth = month.get(month.size() - 1);
            }
        }
        lastSelectedMonth = selectedMonth;
        month_pv.setItems(month, month.indexOf(selectedMonth));
        selectedCalender.set(Calendar.MONTH, englishMonthList.indexOf(selectedMonth));
    }

    /**
     * 初始化日期
     */
    private void initDay(String selectedDay) {
        day.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
        if (!spanYear && !spanMon) {//同年同月
            for (int i = startDay; i <= endDay; i++) {
                day.add(formatDay(i));
            }
        } else if (startYear == selectedYear && startMonth == selectedMonth) {
            for (int i = startDay; i <= selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(formatDay(i));
            }
        } else if (selectedYear == endYear && selectedMonth == endMonth) {
            for (int i = 1; i <= endDay; i++) {
                day.add(formatDay(i));
            }
        } else {
            for (int i = 1; i <= selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(formatDay(i));
            }
        }
        if (TextUtils.isEmpty(selectedDay) || day.indexOf(selectedDay) == -1) {
            if (day.indexOf(lastSelectedDay) != -1){
                selectedDay = lastSelectedDay;
            }else {
                selectedDay = day.get(day.size() - 1);
            }
        }
        lastSelectedDay = selectedDay;
        day_pv.setItems(day, day.indexOf(selectedDay));
        lastSelectedDay = selectedDay;
        selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(selectedDay));
    }

    /**
     * 验证字符串是否是一个合法的日期格式
     */
    private boolean isValidDate(String date, String template) {
        boolean convertSuccess = true;
        // 指定日期格式
        SimpleDateFormat format = new SimpleDateFormat(template);
        if (timeZone != null) {
            format.setTimeZone(timeZone);
        }
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2015/02/29会被接受，并转换成2015/03/01
            format.setLenient(false);
            format.parse(date);
        } catch (Exception e) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    public boolean isShowing() {
        if (datePickerDialog != null && datePickerDialog.isShowing()) {
            return true;
        }
        return false;
    }

    public void dismiss() {
        if (datePickerDialog != null && datePickerDialog.isShowing()) {
            datePickerDialog.dismiss();
        }
    }
}

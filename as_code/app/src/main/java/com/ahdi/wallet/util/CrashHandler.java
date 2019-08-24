package com.ahdi.wallet.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;

import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.utils.UMengUtil;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.wallet.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/6/1.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler{
    private static final String TAG = "CrashHandler";

    private Context context = null;
    private Thread.UncaughtExceptionHandler defaultHandler = null;
    /**
     * 用来存储设备信息和异常信息
     */

    private Map<String, String> infos = new HashMap<String, String>();
    /**
     *  用于格式化日期,作为日志文件名的一部分
     */
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private static String CRASH_LOG_NAME_START = "crash-";
    private static long LOG_SAVE_TIME = 7*24*60*60*1000;

    private static CrashHandler ourInstance = new CrashHandler();
    private CrashHandler() {
    }
    public static CrashHandler getInstance() {
        return ourInstance;
    }

    public void init(Context context){
        this.context = context;
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        autoClear(LOG_SAVE_TIME);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        UMengUtil.reportError(context, e);
        handleException(t,e);
    }

    private void handleException(Thread t, Throwable e){
        if (e == null){
            if(defaultHandler != null){
                defaultHandler.uncaughtException(t, e);
            }
            return;
        }
        try{
            // 使用Toast来显示异常信息
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    ToastUtil.showToastShort(context,  context.getString(R.string.Toast_B0).concat(Constants.LOCAL_APP_CRASH));
                    Looper.loop();
                }
            }.start();
            if (!TextUtils.isEmpty(logFileDir())){
                collectDeviceInfo(context);
                saveCrashInfoFile(e);
            }
            ActivityManager.getInstance().finishAllActivity();
            SystemClock.sleep(2000);
            UMengUtil.onKillProcess(context);
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }catch (Exception ex){
            ex.printStackTrace();
            if(defaultHandler != null){
                defaultHandler.uncaughtException(t, e);
            }
        }
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    private void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                infos.put("versionName", pi.versionName);
                infos.put("versionCode", String.valueOf(pi.versionCode));
            }
        } catch (Exception e){
            e.printStackTrace();
            LogUtil.e(TAG, e.toString());
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, e.toString());
            }
        }
    }

    /**
     * 保存错误信息到文件中
     * @param ex
     * @throws Exception
     */
    private void saveCrashInfoFile(Throwable ex) throws Exception {

        StringBuffer sb = new StringBuffer();
        try {
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sDateFormat.format(new Date());
            sb.append("\r\n" + date + "\n");
            for (Map.Entry<String, String> entry : infos.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append(key + "=" + value + "\n");
            }

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.flush();
            printWriter.close();
            String result = writer.toString();
            LogUtil.e(TAG,result);
            sb.append(result);
            writeFile(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("an error occured while writing file...\r\n");
            writeFile(sb.toString());
        }
    }
    private void writeFile(String sb) throws Exception {
        String time = formatter.format(new Date());
        String fileName = CRASH_LOG_NAME_START + time + ".log";
        String fileDir = logFileDir();
        if (!TextUtils.isEmpty(fileDir)) {
            File file = new File(fileDir,fileName);
            if (!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(sb.getBytes());
            fos.flush();
            fos.close();
        }
    }

    /**
     * 文件删除
     * @param autoClearDay 文件保存时长
     */
    private void autoClear(long autoClearDay) {
        String logFileDir = logFileDir();
        if (!TextUtils.isEmpty(logFileDir)){
            try {
                File logFile = new File(logFileDir);
                File[] files = logFile.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.startsWith(CRASH_LOG_NAME_START);
                    }
                });
                if (files == null || files.length == 0){
                    return;
                }
                for (int i = 0; i < files.length; i++) {
                    String time = files[i].getName().substring(CRASH_LOG_NAME_START.length(),CRASH_LOG_NAME_START.length() + 10);
                    if (System.currentTimeMillis() - formatter.parse(time).getTime() > autoClearDay){
                        files[i].delete();
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    /**
     * 判断sdcard是否可用，目录返回目录路径
     **/
    private String logFileDir() {
        String sDStateString = android.os.Environment.getExternalStorageState();
        String filePath = "";
        if (sDStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
            try {
                File SDFile = android.os.Environment.getExternalStorageDirectory();
                File logDir = new File(SDFile.getAbsolutePath() + File.separator + ConfigCountry.PLATFORMID_FOLDER);
                if (!logDir.exists()) {
                    logDir.mkdir();
                }
                filePath = logDir.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }
}

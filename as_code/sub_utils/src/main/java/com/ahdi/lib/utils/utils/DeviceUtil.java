package com.ahdi.lib.utils.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * @author xiaoniu
 * @date 2018/10/9.
 *
 * 设备信息工具类
 * 包含硬件信息 和 软件信息
 */

public class DeviceUtil {

    private static final String TAG = DeviceUtil.class.getSimpleName();


    public static String getMacAddress(){
        String mac = AppGlobalUtil.getInstance().getMac();
        if (!TextUtils.isEmpty(mac)){
            return mac;
        }
        mac = getMac();
        AppGlobalUtil.getInstance().setMac(mac);
        return mac;
    }

    private static String getMac() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            LogUtil.d("", "获取mac出错：" + ex.toString());
        }
        return "";
    }


    public static String getIMSI() {

        try {
            TelephonyManager mTelephonyMgr = (TelephonyManager) AppGlobalUtil.getInstance().getContext().getSystemService(Context.TELEPHONY_SERVICE);
            String imsi = mTelephonyMgr.getSubscriberId();
            return imsi;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getModel() {
        String model = AppGlobalUtil.getInstance().getModel();
        if (!TextUtils.isEmpty(model)){
            return model;
        }
        model = Build.MODEL;
        AppGlobalUtil.getInstance().setModel(model);
        return model;
    }

    /**
     * 获取手机品牌
     *
     * @return
     */
    public static String getPhoneBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取设备名称
     *
     * @return
     */

    public static String getPhoneName() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            String mDecideNamen = bluetoothAdapter.getName();
            if (!TextUtils.isEmpty(mDecideNamen)) {
                return mDecideNamen;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机Android 版本（4.4、5.0、5.1 ...）
     *
     * @return
     */
    public static String getBuildVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static int getDeviceWidth() {
        return AppGlobalUtil.getInstance().getContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getDeviceHeight() {
        return AppGlobalUtil.getInstance().getContext().getResources().getDisplayMetrics().heightPixels;
    }

    public static String getMeid() {
        String meid = "N/A";
        TelephonyManager manager = (TelephonyManager) AppGlobalUtil.getInstance().getContext().getSystemService(Context.TELEPHONY_SERVICE);

        try {
            Method method = manager.getClass().getMethod("getDeviceId", int.class);

            meid = (String) method.invoke(manager, TelephonyManager.PHONE_TYPE_CDMA);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return meid;
    }


    public static String getAndroidId(Context context) {
        try {
            String androidId = android.provider.Settings.System.getString(
                    context.getContentResolver(), TerminalIdUtil.ANDROID_ID);
            return androidId == null ? "" : androidId;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getAndroidId() {
      return getAndroidId(AppGlobalUtil.getInstance().getContext());
    }

    /**
     * 序列号
     *
     * @return
     */
    public static String getSN() {
        try {
            Class c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            return (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDeviceID() {

        String deviceId = "";
        String imei1 = "";
        String imei2 = "";
        TelephonyManager mTelephonyMgr = null;
        try {
            mTelephonyMgr = (TelephonyManager) AppGlobalUtil.getInstance().getContext().getSystemService(Context.TELEPHONY_SERVICE);
            imei1 = getDoubleImei(mTelephonyMgr, "getDeviceIdGemini", 0);
            imei2 = getDoubleImei(mTelephonyMgr, "getDeviceIdGemini", 1);
        } catch (Exception e) {
            try {
                imei1 = getDoubleImei(mTelephonyMgr, "getDeviceId", 0);
                imei2 = getDoubleImei(mTelephonyMgr, "getDeviceId", 1);
            } catch (Exception ex) {
                LogUtil.e(TAG, "get device id fail: " + e.toString());
            }
        }

        if (!TextUtils.isEmpty(imei1) && !TextUtils.isEmpty(imei2)) {
            deviceId = imei1 + "," + imei2;
        } else if (!TextUtils.isEmpty(imei1)) {
            deviceId = imei1;
        } else if (!TextUtils.isEmpty(imei2)) {
            deviceId = imei2;
        } else if (mTelephonyMgr != null) {
            try {
                deviceId = mTelephonyMgr.getDeviceId();
            } catch (Exception e) {
                LogUtil.i(TAG, "mTelephonyMgr.getDeviceId() fail" + e.toString());
            }
        }
        return deviceId;
    }

    /**
     * 获取双卡手机的imei
     */
    private static String getDoubleImei(TelephonyManager telephony, String predictedMethodName, int slotID) throws Exception {
        String inumeric = null;

        Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
        Class<?>[] parameter = new Class[1];
        parameter[0] = int.class;
        Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
        Object[] obParameter = new Object[1];
        obParameter[0] = slotID;
        Object ob_phone = getSimID.invoke(telephony, obParameter);
        if (ob_phone != null) {
            inumeric = ob_phone.toString();
        }
        return inumeric;
    }

    /**
     * 判断网络是否可用joe true:可用 false:不可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        if (null == context) {
            return false;
        }
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取可用的网络信息
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo != null && networkinfo.isConnected()) {
            // 网络可用
            return true;
        }
        // 网络不可用
        return false;
    }


    public static boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }

    /**
     * 获取app目标版本号
     *
     * @return
     */
    public static int getTargetVersion(Context context) {
        int targetVersion = 0;
        try {
            targetVersion = context.getApplicationInfo().targetSdkVersion;
        } catch (Exception ex) {
            LogUtil.e("tag", ex.toString());
        }
        return targetVersion;
    }

    /**
     * 获取版本号名称
     *
     * @param
     * @return
     */
    public static String getVersionName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verName;
    }

    /**
     * 包名
     * @param context
     * @return
     */
    public static String getApplicationName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static int getScreenWidth(Activity activity) {
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        return width;
    }

    public static int getScreenHeight(Activity activity) {
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int height = outMetrics.heightPixels;
        return height;
    }


    public static String getImei(Context context) {
        String deviceId = "";
        String imei1 = "";
        String imei2 = "";
        TelephonyManager mTelephonyMgr = null;
        try {
            mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei1 = getDoubleImei(mTelephonyMgr, "getDeviceIdGemini", 0);
            imei2 = getDoubleImei(mTelephonyMgr, "getDeviceIdGemini", 1);
        } catch (Exception e) {
            try {
                imei1 = getDoubleImei(mTelephonyMgr, "getDeviceId", 0);
                imei2 = getDoubleImei(mTelephonyMgr, "getDeviceId", 1);
            } catch (Exception ex) {
                LogUtil.i(TAG, "get device id fail" + e.toString());
            }
        }

        if (!TextUtils.isEmpty(imei1) && !TextUtils.isEmpty(imei2)) {
            deviceId = imei1 + "," + imei2;
        } else if (!TextUtils.isEmpty(imei1)) {
            deviceId = imei1;
        } else if (!TextUtils.isEmpty(imei2)) {
            deviceId = imei2;
        } else if (mTelephonyMgr != null) {
            try {
                deviceId = mTelephonyMgr.getDeviceId();
            } catch (Exception e) {
                LogUtil.i(TAG, "mTelephonyMgr.getDeviceId() fail" + e.toString());
            }
        }
        return deviceId;
    }
}

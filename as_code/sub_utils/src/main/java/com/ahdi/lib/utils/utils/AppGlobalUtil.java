package com.ahdi.lib.utils.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.WebView;

import com.ahdi.lib.utils.base.GlobalContextWrapper;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.config.ConfigHelper;
import com.ahdi.lib.utils.config.Constants;
import com.bca.xco.widget.util.XCOUtil;

import java.io.File;

/**
 * 全局数据
 */
public class AppGlobalUtil {

    public final static String TAG = AppGlobalUtil.class.getSimpleName();

    private static AppGlobalUtil instance;

    public static AppGlobalUtil getInstance() {
        if (instance == null) {
            synchronized (AppGlobalUtil.class) {
                if (instance == null) {
                    instance = new AppGlobalUtil();
                }
            }
        }
        return instance;
    }


    private Context context;
    private long timestamp;
    /***----------*/
    private String mac;                 //	mac地址
    private String model;               //	设备型号
    private String tid;
    private String userAgent;
    private String aesPrivateKey;       // aeskey后续用来解密申请绑卡的信息
    private String bcaUserAgent;


    private String sid ;
    private String userID ;
    private String loginName ;

    public Context getContext() {
        return context;
    }

    public void initContext(Context mContext) {
        context = GlobalContextWrapper.wrap(mContext.getApplicationContext(), LanguageUtil.getLanguage(mContext));
        getUserAgent();     // 初始化 userAgent
        getBCAUserAgent();  // 初始化 BCA userAgent
    }

    public final String getPackageName() {
        if (context != null)
            return context.getPackageName();
        else
            return null;
    }


    public void sendBroadcast(Context context, String key, String value, String flag) {
        Intent intent = new Intent(flag);
        intent.setPackage(getPackageName());
        if (!TextUtils.isEmpty(key)) {
            intent.putExtra(key, value);
        }
        context.sendBroadcast(intent);
    }

    public final Object getSystemService(String name) {
        if (context != null)
            return context.getSystemService(name);
        else
            return null;
    }

    public void destroy() {
        instance = null;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public String getAesPrivateKey() {
        return aesPrivateKey;
    }

    public void setAesPrivateKey(String aesPrivateKey) {
        this.aesPrivateKey = aesPrivateKey;
    }

    public String getString(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, "");
    }

    public void putString(Context context, String key, String str) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(key, str).commit();
    }

    public boolean getBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key, false);
    }

    public void putBoolean(Context context, String key, boolean bool) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean(key, bool).commit();
    }

    public void removeKey(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().remove(key).commit();
    }

    public void sendBroadcastIID(Context context, String key, String value, String flag) {
        Intent intent = new Intent(flag);
        intent.putExtra(key, value);
        intent.setPackage(AppGlobalUtil.getInstance().getPackageName());
        context.sendBroadcast(intent);
    }

    public void deleteFiles(String downloadpath) {
        try {
            if (!TextUtils.isEmpty(downloadpath)) {
                LogUtil.e("tag", "---downloadpath--- " + downloadpath);
                File folder = new File(downloadpath);
                File[] files = folder.listFiles();
                if (files == null) {
                    return;
                }
                if (files.length > 0) {
                    for (File file : files) {
                        LogUtil.e("tag", "delete fileName: " + file.getName());
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e("tag", "删除文件出错：" + e.toString());
        }
    }


    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }


    /**
     * 获取 UserAgent
     */
    public String getUserAgent() {

        if (TextUtils.isEmpty(userAgent)) {
            userAgent = getString(context, Constants.LOCAL_KEY_USERAGENT);
        } else {
            return userAgent;
        }

        if (TextUtils.isEmpty(userAgent)) {
            WebView mWebView = new WebView(context);
            userAgent = mWebView.getSettings().getUserAgentString();
            mWebView.destroy();
        } else {
            return userAgent;
        }

        // webview的原始UA  +  getCustomUserAgent()
        userAgent = userAgent + getCustomUserAgent();
        putString(context, Constants.LOCAL_KEY_USERAGENT, userAgent);

        return userAgent;
    }


    /**
     * 更新 UserAgent 规则如下:
     * 传入webview的原始UA
     */
    public String upDateUserAgent(String webviewUserAgent) {

        if (!TextUtils.isEmpty(webviewUserAgent)) {
            // webview的原始UA  +  getCustomUserAgent()
            userAgent = webviewUserAgent + getCustomUserAgent();
        }

        if (!TextUtils.isEmpty(userAgent)) {
            putString(context, Constants.LOCAL_KEY_USERAGENT, userAgent);
        }

        return userAgent;
    }


    //   +   " " + APP名称 + “/” + APP版本号
    private String getCustomUserAgent() {
        String localUserAgent = " " + DeviceUtil.getApplicationName(context) + "/" + ConfigHelper.APP_VERSION_NAME;
        return localUserAgent;
    }

    /**
     * 获取bca sdk deviceID
     *
     * @return
     */
    public String getBCADeviceID() {
        if (context != null) {
            return XCOUtil.getDeviceID(context);
        }
        return "";
    }

    /**
     * 获取bca UA (只能在主线程获取)
     *
     * @return
     */
    public String getBCAUserAgent() {
        if (TextUtils.isEmpty(bcaUserAgent) && context != null) {
            bcaUserAgent = XCOUtil.getUserAgent(context);
        }
        return bcaUserAgent;
    }

    /**
     * 检查手机号 区号62最小8位 其他区号最小7位
     *
     * @param areaCode
     * @param phoneNum
     * @return
     */
    public boolean checkPhoneNum(String areaCode, String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            return false;
        }
        if (TextUtils.equals(areaCode, ConfigCountry.KEY_AREA_CODE)
                && phoneNum.length() < ConfigCountry.PHONE_LIMIT_MIN_LENGTH_8) {
            return false;

        } else if (phoneNum.length() < ConfigCountry.PHONE_LIMIT_MIN_LENGTH_7) {
            return false;

        }
        return true;
    }

    public String getLName(Context context) {
        return getString(context, Constants.LOCAL_LNAME_KEY);
    }


    public void setSID(String sid){
        this.sid = sid ;
        putString(context , Constants.SP_KEY_SID,sid );
    }

    public String getSID() {
        if(TextUtils.isEmpty(sid)){
            sid =  getString(context ,Constants.SP_KEY_SID);
        }
        return sid;
    }
    public void setUserID(String userID){
        this.userID = userID ;
        putString(context , Constants.SP_KEY_USER_ID,userID );
    }

    public String getUserID() {
        if(TextUtils.isEmpty(userID)){
            userID =  getString(context ,Constants.SP_KEY_USER_ID);
        }
        return userID;
    }

    public void setLoginName(String loginName){
        this.loginName = loginName ;
        putString(context , Constants.SP_KEY_LOGIN_NAME,loginName );
    }

    public String getLoginName() {
        if(TextUtils.isEmpty(loginName)){
            loginName =  getString(context ,Constants.SP_KEY_LOGIN_NAME);
        }
        return loginName;
    }
}

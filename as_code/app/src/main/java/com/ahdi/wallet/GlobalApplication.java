package com.ahdi.wallet;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.ahdi.lib.utils.utils.LBSInstance;
import com.ahdi.wallet.app.response.ProfileRsp;
import com.ahdi.wallet.app.schemas.AccountSchema;
import com.ahdi.wallet.app.schemas.UserSchema;
import com.ahdi.lib.utils.bean.AccountData;
import com.ahdi.lib.utils.bean.UserData;
import com.ahdi.lib.utils.config.ConfigHelper;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.LanguageUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;
import com.ahdi.lib.utils.utils.UMengUtil;
import com.ahdi.wallet.app.callback.ActivityLifeCallBack;
import com.ahdi.wallet.tts.TTSPlayer;
import com.ahdi.wallet.util.CrashHandler;

public class GlobalApplication extends MultiDexApplication {

    public static final String TAG = GlobalApplication.class.getSimpleName();
    private static GlobalApplication instance = null;


    private String sid ;
    private String userID ;

    private ActivityLifeCallBack activityLifeCallBack = null;
    private UserData userData;
    private AccountData accountData;

    public static GlobalApplication getApplication() {
        return instance;
    }

    @Override
    public void onCreate() {
        //应用程序启动时被系统调用
        super.onCreate();
        if (instance == null) {
            LogUtil.d(TAG, "GlobalApplication  onCreate");
            instance = this;
            /**获取网络状态需要提前初始化 + 初始化UserAgent*/
            AppGlobalUtil.getInstance().initContext(this);

            LBSInstance.getInstance().initLBS(this);

            //  initTTS(this);
            registerActivityLiftCallBack();
            /**debug时在logcat看崩溃日志，打包之后在sd卡看崩溃日志*/
            if (ConfigHelper.CRASH_LOG) {
                CrashHandler.getInstance().init(this);
            }
            UMengUtil.initUMeng(this);
            LanguageUtil.cleanRecreateState(AppGlobalUtil.getInstance().getContext());
        }
    }

    public void setSID(String sid){
        this.sid = sid ;
        AppGlobalUtil.getInstance().putString(this , Constants.LOCAL_KEY_SID,sid );
    }

    public String getSID() {
        if(TextUtils.isEmpty(sid)){
            sid =  AppGlobalUtil.getInstance().getString(this ,Constants.LOCAL_KEY_SID );
        }
        return sid;
    }
    public void setUserID(String userID){
        this.userID = userID ;
    }

    public String getUserID() {
        return userID;
    }

    public void updateUserSchema(UserSchema userSchema) {
        if (userData == null) {
            userData = new UserData();
        }
        if (userSchema != null) {
            String areaCode = userSchema.AreaCode;
            String lName = userSchema.LName;
            if (!TextUtils.isEmpty(lName)) {
                userData.setLName(lName);

                AppGlobalUtil.getInstance().putString(getApplicationContext(), Constants.LOCAL_LNAME_KEY, lName);
                AppGlobalUtil.getInstance().putString(getApplicationContext(), Constants.LOCAL_AREA_CODE_KEY, areaCode);
            }
            if (!TextUtils.isEmpty(userSchema.NName)) {
                userData.setNName(userSchema.NName);
                AppGlobalUtil.getInstance().putString(getApplicationContext(), Constants.LOCAL_NNAME_KEY, userSchema.NName);
            }
            if (!TextUtils.isEmpty(userSchema.sLName)) {
                userData.setsLName(userSchema.sLName);
                AppGlobalUtil.getInstance().putString(getApplicationContext(), Constants.LOCAL_SLNAME_KEY, userSchema.sLName);
            }
            if (!TextUtils.isEmpty(String.valueOf(userSchema.uid))) {
                userData.setUID(String.valueOf(userSchema.uid));
            }
            if (!TextUtils.isEmpty(userSchema.Avatar)) {
                userData.setAvatar(userSchema.Avatar);
                String LName = AppGlobalUtil.getInstance().getString(getApplicationContext(), Constants.LOCAL_LNAME_KEY);
                TouchIDStateUtil.setTouchIDUnlockAvatar(getApplicationContext(), LName, userSchema.Avatar);
            }
            if (!TextUtils.isEmpty(userSchema.Voucher)) {
                //根据文档Voucher可能为空： Voucher	条件	String	免密码登录凭证，如果是登录名+ 密码登录，则本字段必有
                userData.setVoucher(userSchema.Voucher);
                AppGlobalUtil.getInstance().putString(getApplicationContext(), Constants.LOCAL_VOUCHER_KEY, userSchema.Voucher);
            }
            userData.setRNA(userSchema.isAuth);
            if (!TextUtils.isEmpty(userSchema.email)) {
                userData.setEmail(userSchema.email);
            }
            if (userSchema.gender > 0) {
                userData.setGender(userSchema.gender);
            }
            userData.setBirthday(userSchema.birthday);
        }
        ProfileUserUtil.getInstance().setUserData(userData);
    }

    public void updateAccountSchema(AccountSchema accountSchema) {
        if (accountData == null) {
            accountData = new AccountData();
        }
        if (accountSchema != null) {
            if (!TextUtils.isEmpty(accountSchema.status)) {
                accountData.setStatus(accountSchema.status);
            }
            if (!TextUtils.isEmpty(accountSchema.balance)) {
                accountData.setBalance(accountSchema.balance);
            }
        }
        ProfileUserUtil.getInstance().setAccountData(accountData);
    }

    public void updateAccountBalance(String balance) {
        if (accountData == null) {
            accountData = new AccountData();
        }
        if (!TextUtils.isEmpty(ProfileUserUtil.getInstance().getAccountStatus())) {
            accountData.setStatus(ProfileUserUtil.getInstance().getAccountStatus());
        }
        if (!TextUtils.isEmpty(balance)) {
            accountData.setBalance(balance);
        }
        ProfileUserUtil.getInstance().setAccountData(this.accountData);
    }

    public void updateAccountStatus(String status) {
        if (accountData == null) {
            accountData = new AccountData();
        }
        if (!TextUtils.isEmpty(ProfileUserUtil.getInstance().getAccountBalance())) {
            accountData.setBalance(ProfileUserUtil.getInstance().getAccountBalance());
        }
        if (!TextUtils.isEmpty(status)) {
            accountData.setStatus(status);
        }
        ProfileUserUtil.getInstance().setAccountData(this.accountData);
    }

    private void initTTS(Context context) {
        TTSPlayer.getTTSPlayer(context).initTTS(new TTSPlayer.TTSPlayerListener() {
            @Override
            public void onCallBackInitSuccess() {

            }

            @Override
            public void onCallBackInitError(int status) {

            }
        });
    }

    private void registerActivityLiftCallBack() {
        if (activityLifeCallBack == null) {
            activityLifeCallBack = new ActivityLifeCallBack();
        }
        registerActivityLifecycleCallbacks(activityLifeCallBack);
    }

    public boolean isForeground() {
        if (activityLifeCallBack == null) {
            return false;
        }
        return activityLifeCallBack.isForeground();
    }

    public void updateProfile(ProfileRsp response) {
        updateUserSchema(response.getUserSchema());
        updateAccountSchema(response.getAccountSchema());
        ProfileUserUtil.getInstance().setCardNum(response.getCardNum());
        ProfileUserUtil.getInstance().setAccNum(response.getAccNum());
        ProfileUserUtil.getInstance().setHasMsg(response.isHasMsg());
        ProfileUserUtil.getInstance().setRnaSw(response.isRnaSw);
        ProfileUserUtil.getInstance().setWithdraw(response.isWithdraw);
        ProfileUserUtil.getInstance().setNeedRNA(response.isNeedRNA);
        ProfileUserUtil.getInstance().setHasPwd(response.hasPwd);
    }
}
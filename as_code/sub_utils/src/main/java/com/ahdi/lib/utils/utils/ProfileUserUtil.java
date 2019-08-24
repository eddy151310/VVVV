package com.ahdi.lib.utils.utils;

import com.ahdi.lib.utils.bean.AccountData;
import com.ahdi.lib.utils.bean.UserData;

public class ProfileUserUtil {

    private static ProfileUserUtil instance;

    private ProfileUserUtil() {
    }

    public static ProfileUserUtil getInstance() {
        if (instance == null) {
            synchronized (ProfileUserUtil.class) {
                if (instance == null) {
                    instance = new ProfileUserUtil();
                }
            }
        }
        return instance;
    }

    private UserData userData;
    private AccountData accountData;
    private int cardNum;
    private int accNum;
    private boolean hasMsg;
    private String sid;
    private boolean isWithdraw;     // 提现开关
    private boolean isRnaSw;        // 实名认证开关
    private boolean isNeedRNA;      // 是否需要实名认证
    private boolean isRNA;          // 实名认证状态
    private boolean isHasPwd;       // 是否有登录密码

    public void setUserData(UserData userData) {
        this.userData = userData;
        isRNA = userData.isRNA();
    }
    public String getSID() {
        return this.sid;
    }

    public void setSID( String sid) {
        this.sid = sid ;
    }

    public UserData getUserData() {
        return userData;
    }

    public String getUserID(){
        if (userData == null){
            return "";
        }
        return userData.getUID();
    }

    public void setAccountData(AccountData accountData) {
        this.accountData = accountData;
    }

    public AccountData getAccountData() {
        return accountData;
    }

    public String getAccountStatus() {
        if (accountData != null) {
            return accountData.getStatus();
        }
        return "";
    }

    public String getAccountBalance() {
        if (accountData != null) {
            return accountData.getBalance();
        }
        return "";
    }

    public int getCardNum() {
        return cardNum;
    }

    public void setCardNum(int cardNum) {
        this.cardNum = cardNum;
    }

    public int getAccNum() {
        return accNum;
    }

    public void setAccNum(int accNum) {
        this.accNum = accNum;
    }

    public boolean isHasMsg() {
        return hasMsg;
    }

    public void setHasMsg(boolean hasMsg) {
        this.hasMsg = hasMsg;
    }

    public boolean isNeedRNA() {
        return isNeedRNA;
    }

    public void setNeedRNA(boolean needRNA) {
        isNeedRNA = needRNA;
    }

    public boolean isWithdraw() {
        return isWithdraw;
    }

    public void setWithdraw(boolean withdraw) {
        isWithdraw = withdraw;
    }

    public boolean isRnaSw() {
        return isRnaSw;
    }

    public void setRnaSw(boolean rnaSw) {
        isRnaSw = rnaSw;
    }

    public boolean isRNA() {
        return isRNA;
    }

    public void setRNA(boolean RNA) {
        isRNA = RNA;
    }

    public boolean isHasPwd() {
        return isHasPwd;
    }

    public void setHasPwd(boolean hasPwd) {
        isHasPwd = hasPwd;
    }

    public void cleanAccountUserData() {

        if (accountData != null) {
            accountData.cleanAccountData();
            accountData = null;
        }

        if (userData != null) {
            userData.cleanUserData();
            userData = null;
        }

        onDestroy();
    }

    public void onDestroy() {
        instance = null;
    }
}

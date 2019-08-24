package com.ahdi.lib.utils.bean;

import com.ahdi.lib.utils.utils.DateUtil;

/**
 * Date: 2018/5/30 上午10:30
 * Author: kay lau
 * Description:
 */
public class UserData {

    /**
     * User_schema
     */
    private String NName;
    private String LName;
    private String UID;
    private String Avatar;
    private String Voucher;
    private boolean RNA;
    private int gender;
    private String birthday;
    private String email;
    private String sLName;

    public String getNName() {
        return NName;
    }

    public void setNName(String NName) {
        this.NName = NName;
    }

    public String getLName() {
        return LName;
    }

    public void setLName(String LName) {
        this.LName = LName;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public String getVoucher() {
        return Voucher;
    }

    public void setVoucher(String voucher) {
        Voucher = voucher;
    }

    public boolean isRNA() {
        return RNA;
    }

    public void setRNA(boolean RNA) {
        this.RNA = RNA;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = DateUtil.yyyyMMdd2ddMMyyyy(birthday);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getsLName() {
        return sLName;
    }

    public void setsLName(String sLName) {
        this.sLName = sLName;
    }

    public void cleanUserData() {
        setLName("");
        setNName("");
        setUID("");
        setAvatar("");
        setVoucher("");
        setRNA(false);
        setBirthday("");
        setEmail("");
        setGender(0);
        setsLName("");
    }
}


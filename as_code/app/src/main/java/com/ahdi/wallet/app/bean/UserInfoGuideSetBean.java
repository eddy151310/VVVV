package com.ahdi.wallet.app.bean;

import android.text.TextUtils;

import com.ahdi.lib.utils.config.Constants;

/**
 * Date: 2018/7/2 下午3:26
 * Author: kay lau
 * Description:
 */
public class UserInfoGuideSetBean {

    private String nname;
    private String bithdate;
    private String gender;
    private String email;

    public UserInfoGuideSetBean() {
    }

    public UserInfoGuideSetBean(String nname, String bithdate, String gender, String email) {
        this.nname = nname;
        this.bithdate = bithdate;
        this.gender = gender;
        this.email = email;
    }

    public String getNname() {
        return nname;
    }

    public String getBithdate() {
        return bithdate;
    }

    public int getGender() {
        if (TextUtils.equals(Constants.LOCAL_FEMALE_KEY, gender)) {
            return Constants.LOCAL_GENDER_FEMALE;
        }
        return Constants.LOCAL_GENDER_MALE;
    }

    public String getEmail() {
        return email;
    }
}

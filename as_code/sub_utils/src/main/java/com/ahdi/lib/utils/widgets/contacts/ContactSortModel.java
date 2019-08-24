package com.ahdi.lib.utils.widgets.contacts;

import android.graphics.Bitmap;

public class ContactSortModel {

    private long id;
    private String name;
    private String mobile;
    private Bitmap img;
    private String sortLetters;//显示数据拼音的首字母

    public ContactSortModel() {
        super();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public Bitmap getImg() {
        return img;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}

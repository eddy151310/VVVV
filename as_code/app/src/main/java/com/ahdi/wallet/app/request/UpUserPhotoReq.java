package com.ahdi.wallet.app.request;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONObject;

/**
 * @author zhaohe
 * 上传头像
 */
public class UpUserPhotoReq extends Request {

    private String upURL = "";
    private byte[] imgByte;

    public UpUserPhotoReq(String upURL, byte[] imgByte) {
        this.upURL = upURL;
        this.imgByte = imgByte;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }
        LogUtil.e("UpUserPhotoReq", "UpUserPhotoReq: " + json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }

    public String getUpURL() {
        return upURL;
    }

    public void setUpURL(String upURL) {
        this.upURL = upURL;
    }

    public byte[] getImgByte() {
        return imgByte;
    }

    public void setImgByte(byte[] imgByte) {
        this.imgByte = imgByte;
    }
}

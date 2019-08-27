package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 实名认证审核结果响应
 * @author zhaohe
 */
public class UpUserPhotoRsp extends Response {

    //    参数名	值类型	说明
    //    moudle	string	归属模块
    //    oname	    string	文件原始名称
    //    sname	    string	存储文件名称
    //    ctime	    long	创建时间
    //    suffix	string	文件后缀
    //    md5	    string	文件md5值
    //    size	    long	文件大小
    //    relativePath	string	文件存储相对路径
    //    widthHeight	string	图片宽x高（像素）

    private String moudle  ="";
    private String oname  ="";
    private String sname  ="";
    private long ctime = -1;
    private String suffix  ="";
    private String md5  ="";
    private long size = -1;
    private String relativePath  ="";
    private String widthHeight  ="";

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null){
            return;
        }
        mHeader.retCode = json.optString("code");
        mHeader.retMsg = json.optString("msg");

        JSONArray bodyArray = json.optJSONArray("content");
        if (bodyArray == null) {
            return;
        }
        JSONObject body = null;
        try {
            body = bodyArray.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (body == null) {
            return;
        }
        moudle = body.optString("moudle");
        oname = body.optString("oname");
        sname = body.optString("sname");
        ctime = body.optLong("ctime");
        suffix = body.optString("suffix");
        md5 = body.optString("md5");
        size = body.optLong("size");
        md5 = body.optString("md5");
        relativePath = body.optString("relativePath");
        widthHeight = body.optString("widthHeight");
    }

    public String getMoudle() {
        return moudle;
    }

    public String getOname() {
        return oname;
    }

    public String getSname() {
        return sname;
    }

    public long getCtime() {
        return ctime;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getMd5() {
        return md5;
    }

    public long getSize() {
        return size;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public String getWidthHeight() {
        return widthHeight;
    }
}

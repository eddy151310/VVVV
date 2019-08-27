package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * 获取图片上传地址的响应
 * @author zhaohe
 */
public class GetPhotoUpUrlRsp extends Response {

    //    字段名	重要性	类型	描述
    //    Url	    必须	String	上传头像的地址
    //    Type	    必须	int	头像服务器Type

    private String url = "";
    private int type = -1;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null){
            return;
        }
        JSONObject body = json.optJSONObject(content);
        if (body == null) {
            return;
        }
        url = body.optString("Url");
        type = body.optInt("Type");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

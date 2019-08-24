package com.ahdi.wallet.app.schemas;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 头像信息
 * @author zhaohe
 */
public class AvatarSchema extends ABSIO {

    //    字段名	    重要性	属性	描述
    //    RelativePath	必须	String	上传头像成功后返回的相对路径
    //    Type	        必须	Integer	图片服务器类型，取自图片上传路径接口的Type值

    private String relativePath = "";
    private int type = -1;

    public AvatarSchema(String relativePath, int type) {
        this.relativePath = relativePath;
        this.type = type;
    }

    @Override
    public void readFrom(JSONObject json) throws JSONException {
      return;
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        if (json == null) {
            return null;
        }
        JSONObject schemaJson = new JSONObject();
        if (!TextUtils.isEmpty(relativePath)){
            schemaJson.put("RelativePath", relativePath);
        }
        if (type > -1){
            schemaJson.put("Type", type);
        }
        json.put("Avatar", schemaJson);
        return json;
    }
}

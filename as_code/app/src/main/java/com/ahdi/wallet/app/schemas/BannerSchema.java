package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 首页banner
 */
public class BannerSchema extends ABSIO {

    //    字段名	重要性	属性	描述
    //    Content	必须	String	展示内容
    //    Type	必须	Integer	展示内容类型,( 1-图片，2-文字)
    //    Click	必须	String	url地址

    private String content;
    private int type;
    private String click;

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        content = json.optString("Content");
        type = json.optInt("Type");
        click = json.optString("Click");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }
}

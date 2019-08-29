package com.ahdi.wallet.app.response.aaa;

import com.ahdi.wallet.network.framwork.Response;

import org.json.JSONObject;

/**
 * Author: ibb
 * Description:
 */
public class UserCenterRsp extends Response {

    public String userId;
    public String nickName;
    public String gender;
    public String headPortrait;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject content = json.optJSONObject(Response.content);
        if (content != null) {
            userId = content.optString("userId") ;
            nickName =  "nickName --- test databinding" ; //content.optString("nickName");
            gender = content.optString("gender");
            headPortrait =  content.optString("headPortrait");//"http://img1.imgtn.bdimg.com/it/u=48521173,3854391570&fm=11&gp=0.jpg");
        }
    }

}

package com.ahdi.wallet.app.schemas;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.cryptor.aes.AesKeyCryptor;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 作者：lixue on 2019/1/14 18:20
 */

public class PayBindSchema extends ABSIO {

    private static final String TAG = PayBindSchema.class.getSimpleName();

    /**
     * BCA SDK绑卡拉起参数如下：
     {
     "md5":"",
     "content":{"merchantId":"",
     "apiKey":"",
     "apiSecret":"",
     "clientId":"", "clientSecret":"", "accessToken":""
     }
     }

     */
    private String parm;
    private String invoke;//绑卡通道调起方式。WEB、SDK等
    private String chanelID;//绑卡通道ID编号,Invoke=SDK时，根据该值拉起不同SDK

    private String accessToken;
    private String clientId;
    private String clientSecret;
    private String apiKey;
    private String apiSecret;
    private String merchantId;
    private String xcoid;
    private String uid;

    private String type;
    private String url;
    private String bt;
    private String cv;
    private String pi;
    private String ppt;
    private String sk;
    private String env;


    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if(json == null){
            return;
        }

        parm = json.getString("Param");
        invoke = json.getString("Invoke");
        chanelID = json.getString("ChannelID");

        JSONObject paramJs = new JSONObject(parm);
        if (invoke.equals("SDK")){
            try {
                String payParamEncrypt = paramJs.optString("content");
                String payParamDecrypt = AesKeyCryptor.decodeData(payParamEncrypt);
                LogUtil.d(TAG,"BindBC--Param--解密后的data:" + payParamDecrypt);

                JSONObject payParam = new JSONObject(payParamDecrypt);
                accessToken = payParam.optString("accessToken");
                clientId = payParam.optString("clientId");
                clientSecret = payParam.optString("clientSecret");
                apiKey = payParam.optString("apiKey");
                apiSecret = payParam.optString("apiSecret");
                merchantId = payParam.optString("merchantId");
                xcoid = payParam.optString("xcoid");
                uid = payParam.optString("uid");
                env = payParam.optString("env");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            type = paramJs.getString("type");
            url = paramJs.optString("url");
            JSONObject param = paramJs.optJSONObject("params");
            bt = param.optString("bt");
            cv = param.optString("cv");
            pi = param.getString("pi");
            ppt = param.getString("ppt");
            sk = param.getString("sk");
        }
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

    public String getParm() {
        return parm;
    }

    public String getAccessToken(){
       return accessToken;
    }

    public String getInvoke() {
        return invoke;
    }

    public String getChanelID() {
        return chanelID;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getXcoid() {
        return xcoid;
    }

    public String getUrl() {
        if (TextUtils.equals(type,Constants.REQUEST_METHOD_GET)){
            StringBuilder sb = new StringBuilder();
            sb.append(url).append("?");
            sb.append("cv=").append(cv).append("&");
            sb.append("bt=").append(bt).append("&");
            sb.append("pi=").append(pi).append("&");
            sb.append("ppt=").append(ppt).append("&");
            sb.append("sk=").append(sk);
            url = sb.toString();
        }
        return url;
    }

    public String getUid() {
        return uid;
    }

    public String getEnv() {
        return env;
    }
}

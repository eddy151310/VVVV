package com.ahdi.wallet.module.payment.topup.schemas;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Date: 2019/5/28 下午6:04
 * Author: kay lau
 * Description:
 */
public class RechrTypeSchema extends ABSIO {

    public String Key;                  // 充值类型标识
    public String Title;                // 类型名称
    public String Desc;                 // 类型描述
    public String Icon;                 // 类型icon图标
    private String rechrTypeGroupName;  // 分组名称
    private boolean isHiddenBottomLine;   // 是否隐藏每个条目的底线, false不隐藏, true隐藏.

    /**
     * 调起方式：disable-功能待开放，不可点击；native-本地直接打开；web-URL链接。Invoke=web/native时调起参数见"Param"属性值。
     */
    public String Invoke;

    /**
     * 调起方式参数值，Invoke=web/native时该参数有值。
     * WEB调起参数格式：
     * {"type":"POST/GET","url":"http://xxx","params":{"key1":"value1"},"chartset":"UTF-8"}
     * Get请求数据拼接格式：
     * http://xxx?key1=urlencode(value1)&key2=urlencode(value2)
     * native调起参数格式：
     * ahdipay ://[module]? xxxxxxx
     * 具体协议地址需要根据具体业务进行补充。
     */
    public String Param;
    public String url;

    @Override
    public void readFrom(JSONObject json) throws JSONException {

        if (json == null) {
            return;
        }
        Key = json.optString("Key");
        Title = json.optString("Title");
        Desc = json.optString("Desc");
        Icon = json.optString("Icon");
        Invoke = json.optString("Invoke");
        Param = json.optString("Param");

        if (!TextUtils.isEmpty(Param)) {
            JSONObject paramJs = new JSONObject(Param);
            String type = paramJs.optString("type");
            url = paramJs.optString("url");
            if (TextUtils.equals(type, Constants.REQUEST_METHOD_GET)) {
                String paramsStr = paramJs.optString("params");
                if (!TextUtils.isEmpty(paramsStr)) {
                    Map paramsMap = com.alibaba.fastjson.JSONObject.parseObject(paramsStr, Map.class);
                    url = StringUtil.buildHttpGetUrlParmas(url, paramsMap);
                }
            }
        }
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

    public String getRechrTypeGroupName() {
        return rechrTypeGroupName;
    }

    public void setRechrTypeGroupName(String rechrTypeGroupName) {
        this.rechrTypeGroupName = rechrTypeGroupName;
    }

    public boolean isHiddenBottomLine() {
        return isHiddenBottomLine;
    }

    public void setHiddenBottomLine(boolean hiddenBottomLine) {
        isHiddenBottomLine = hiddenBottomLine;
    }
}

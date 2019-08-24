package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 优惠券
 */
public class VoucherSchema extends ABSIO {

    //    字段名	重要性	属性	描述
    //    Id	    必须	String	券id标识
    //    Name	    必须	String	券名称
    //    Money	    必须	String	券面额（原始金额，单位：基本单位，无格式化数据）
    //    Select	可选	Integer	券使用状态及选择状态；0-不可选， 1-可选不选中， 2-可选并选中
    //    Status	必须	Int	    券状态(1-有效；2-已使用；3-过期)
    //    BizLimits	必须	String[]	券使用业务限制：min_amount -最小交易金额限制；biz_type-用券业务限制；
                                                            //    exp_date -有效期限制，格式：dd/MM/yyyy – dd/MM/yyyy；
                                                            //    pay_type-支付方式限制;
    //    Desc  	可选	Map<int,String>	券信息描述文案<位置，文案>；
    //    Icon	    必须	String	券图标url地址

    private String id;
    private String name;
    private String money;
    private int select;
    public int status;
    private List<String> limits;
//    private JSONObject desc;
    private String icon;

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        id = json.optString("Id");
        name = json.optString("Name");
        money = json.optString("Money");
        select = json.optInt("Select");
        status = json.optInt("Status");
        jsonToList(json.optJSONArray("BizLimits"));
//        desc = json.optJSONObject("Desc");
        icon = json.optString("Icon");
    }

    private void jsonToList(JSONArray jsonArray) {
        if (jsonArray == null){
            return;
        }
        limits = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++){
            limits.add(jsonArray.optString(i));
        }
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMoney() {
        return money;
    }

    public int getSelect() {
        return select;
    }

    public int getStatus() {
        return status;
    }

    public List<String> getLimits() {
        return limits;
    }

    public String getIcon() {
        return icon;
    }
}

package com.ahdi.wallet.app.schemas;

import com.ahdi.wallet.network.framwork.ABSIO;

import org.json.JSONException;
import org.json.JSONObject;

public class ScanCheckSchema extends ABSIO {

    public  String OP_TYPE_B = "B" ;
    public  String OP_TYPE_C = "C" ;

    public  String TYPE_PayCode = "PayCode" ;
    public  String TYPE_TRANSFER = "native::transfer" ;


    public String OP = "";  //操作类型：
                            //B：阻塞（block），不能继续操作
                            //C：继续操作（continue）
    public String Data = "";
    public String Type = ""; //PayCode：pay码（OP为B时可根据该类型进行特殊处理）
    public String Msg = "";

    @Override
    public void readFrom(JSONObject json) throws JSONException {
        if (json == null) {
            return;
        }
        OP = json.optString("OP");
        Data = json.optString("Data");
        Type = json.optString("Type");
        Msg = json.optString("Msg");
    }

    @Override
    public JSONObject writeTo(JSONObject json) throws JSONException {
        return null;
    }

}

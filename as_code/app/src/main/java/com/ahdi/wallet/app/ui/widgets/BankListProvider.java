package com.ahdi.wallet.app.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ahdi.wallet.app.schemas.BankSchema;
import com.ahdi.wallet.app.utils.ConstantsPayment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ktt on 2018/5/2.
 */

public class BankListProvider {
    private HashMap<Integer, BankSchema> bankList = new HashMap<>();
    private static BankListProvider provider;
    private Context context;
    private SharedPreferences bankSp;
    private SharedPreferences.Editor editor;

    private BankListProvider(Context context) {
        this.context = context.getApplicationContext();
        bankSp = context.getSharedPreferences("bankInfo", Context.MODE_PRIVATE);
        editor = bankSp.edit();
    }

    public static synchronized BankListProvider getProvider(Context context) {
        if (provider == null) {
            provider = new BankListProvider(context);
        }
        return provider;
    }


    public synchronized void clearAll() {
        bankList.clear();
        editor.clear();
        editor.commit();
    }
    public void add(BankSchema bankInfo){
        JSONObject bankObj = new JSONObject();
        try {
            bankObj.put("Code", bankInfo.getCode());
            bankObj.put("Name", bankInfo.getName());
            bankList.put(Integer.parseInt(bankInfo.getCode()),bankInfo);
            editor.putString(bankInfo.getCode(), bankObj.toString());
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public synchronized List<BankSchema> getBankInfos() {
        List<BankSchema> bankInfos = new ArrayList<BankSchema>();
        try {
            if (bankSp.getAll().size() > 0) {
                for (String key : bankSp.getAll().keySet()) {
                    JSONObject object = new JSONObject(bankSp.getString(key, ""));
                    BankSchema bankInfo = new BankSchema();
                    bankInfo.setCode(object.getString("Code"));
                    bankInfo.setName(object.getString("Name"));
                    bankInfos.add(bankInfo);
                }
                //发送广播更新banklist
                Intent intent = new Intent(ConstantsPayment.ACTION_GET_BANK_LIST);
                context.sendBroadcast(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bankInfos;
    }
}

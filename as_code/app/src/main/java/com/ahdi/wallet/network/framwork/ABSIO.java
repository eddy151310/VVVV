package com.ahdi.wallet.network.framwork;

import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;

public abstract class ABSIO implements Serializable {

    private static final String TAG = ABSIO.class.getSimpleName();

    private static final long serialVersionUID = -2162335062481229878L;

    @SuppressWarnings("unchecked")
    public static <T extends ABSIO> T[] decodeSchemaArray(Class<T> clazz, String key, JSONObject json) {
        if (json != null && json.has(key)) {
            try {
                JSONArray rsc = json.getJSONArray(key);
                T[] list = (T[]) Array.newInstance(clazz, rsc.length());
                for (int i = 0; i < rsc.length(); i++) {
                    list[i] = clazz.newInstance();
                    list[i].readFrom((JSONObject) rsc.get(i));
                }

                return list;
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.e(TAG, "error:" + e.toString());

            } catch (InstantiationException e) {
                e.printStackTrace();
                LogUtil.e(TAG, "error:" + e.toString());

            } catch (IllegalAccessException e) {
                e.printStackTrace();
                LogUtil.e(TAG, "error:" + e.toString());

            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "error:" + e.toString());
            }
        }

        return null;
    }

    public static <T extends ABSIO> T decodeSchema(Class<T> clazz, JSONObject datas) {
        if (null != datas && datas.length() > 0) {
            try {
                T t = clazz.newInstance();
                t.readFrom(datas);
                return t;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                LogUtil.e(TAG, "error:" + e.toString());

            } catch (InstantiationException e) {
                e.printStackTrace();
                LogUtil.e(TAG, "error:" + e.toString());

            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.e(TAG, "error:" + e.toString());

            } catch (Exception e) {
                LogUtil.e(TAG, "error:" + e.toString());
            }
        }

        return null;
    }

    public abstract void readFrom(JSONObject json) throws JSONException;

    public abstract JSONObject writeTo(JSONObject json) throws JSONException;
}

package com.ahdi.lib.utils.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.ahdi.lib.utils.bean.SchemaBean;
import com.ahdi.lib.utils.config.ConfigCountry;

import java.util.HashMap;
import java.util.Map;


public class SchemaUtil {

    private static final String TAG = SchemaUtil.class.getSimpleName();

    //schema
    private static final String SCHEMA_URL_HTTP = "http";
    private static final String SCHEMA_URL_HTTPS = "https";
    private static final String SCHEMA_URL_CURRENT_APP = ConfigCountry.SCHEMA_APP; //当前app特殊 Schema


    //authority
    private static final String AUTHORITY_PAY = "pay";
    private static final String AUTHORITY_TRANSFER = "transfer";
    private static final String AUTHORITY_BALANCE = "balance";
    private static final String AUTHORITY_BBC = "bbc";

    //path
    private static final String PATH_BBC_BACK = "/back";
    private static final String PATH_BBC_SSO = "/sso";

    // 业务类型（schema + authority + path）
    public static final int TYPE_DEFAULT = 1000;
    public static final int TYPE_SCAN_PAY = 1001;
    public static final int TYPE_TRANSFER = 1002;  // 同TYPE_BALANCE 一样~~~   banner 点击跳转 -- 和扫码跳转【没有关系】   扫码转账 请查看ScanCheckSchema
    public static final int TYPE_BALANCE = 1003;
    public static final int TYPE_BBC_BACK = 1004;
    public static final int TYPE_BBC_SSO = 1005;
    public static final int TYPE_HTTP = 1006;

    /**
     * 判断是否为http/https URL
     *
     * @param url
     * @return
     */
    public static boolean isHttpSchema(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        if (URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
            return true;
        }
        return false;
    }

    //只对 schema  做不区分大小写
    public static SchemaBean doScheme(String url) {
        SchemaBean schemaBean = new SchemaBean();
        if (TextUtils.isEmpty(url)) {
            schemaBean.setType(TYPE_DEFAULT);
            return schemaBean;
        }

        Uri mUri = Uri.parse(url);

        try {
            String schema = mUri.getScheme().toLowerCase();
            LogUtil.e(TAG, "---URL解析----schema:" + schema);

            if (TextUtils.equals(schema, SCHEMA_URL_HTTP)
                    || TextUtils.equals(schema, SCHEMA_URL_HTTPS)) {
                schemaBean.setType(TYPE_HTTP);

            } else if (SCHEMA_URL_CURRENT_APP.equals(schema)) {
                String authority = mUri.getAuthority();
                LogUtil.e(TAG, "---URL解析----authority:" + authority);

                String path = mUri.getPath();
                LogUtil.e(TAG, "---URL解析----path:" + path);

                String query = mUri.getQuery();
                LogUtil.e(TAG, "---URL解析----query:" + query);

                if (AUTHORITY_PAY.equals(authority)) {
                    schemaBean.setParams(query);
                    schemaBean.setType(TYPE_SCAN_PAY);
                } else if (AUTHORITY_TRANSFER.equals(authority)) {
                    schemaBean.setParams(query);
                    schemaBean.setType(TYPE_TRANSFER);

                } else if (AUTHORITY_BALANCE.equals(authority)) {
                    schemaBean.setType(TYPE_BALANCE);

                } else if (AUTHORITY_BBC.equals(authority)) {
                    schemaBean.setParams(query);
                    if (PATH_BBC_BACK.equals(path)) {
                        schemaBean.setType(TYPE_BBC_BACK);

                    } else if (PATH_BBC_SSO.equals(path)) {
                        schemaBean.setType(TYPE_BBC_SSO);
                    }
                } else {
                    schemaBean.setType(TYPE_DEFAULT);
                }
            } else {
                schemaBean.setType(TYPE_DEFAULT);
            }
        } catch (Exception e) {
            LogUtil.d(TAG, "SchemaUtil URL解析失败：" + e.toString());
            schemaBean.setType(TYPE_DEFAULT);
        }
        return schemaBean;
    }

    /**
     * 解析url query 参数, 返回解析完成的map
     *
     * @param param
     * @return
     */
    public static Map<String, Object> parsUrlParams(String param) {
        if (TextUtils.isEmpty(param)) {
            return null;
        }
        Map<String, Object> map = new HashMap<>(0);
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
        return map;
    }
}

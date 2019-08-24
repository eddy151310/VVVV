package com.ahdi.wallet.network;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.ahdi.lib.utils.config.ConfigHelper;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.network.HttpReqTaskListener;
import com.ahdi.lib.utils.network.HttpUtil;
import com.ahdi.lib.utils.security.ClientException;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.DeviceUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.network.framwork.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Date: 2019/6/18 下午3:06
 * Author: kay lau
 * Description:
 */
public class HttpReqAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = HttpReqAsyncTask.class.getSimpleName();

    private Param param;
    private boolean netConnect = false;

    public HttpReqAsyncTask(HttpReqAsyncTask.Param param) {
        this.param = param;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        netConnect = DeviceUtil.isNetworkAvailable(AppGlobalUtil.getInstance().getContext());
    }

    private String doAutoWapSend(final String module, String paramStr) {
        String httpUrl = ConfigHelper.URL + module;

        if (TextUtils.isEmpty(httpUrl) || TextUtils.isEmpty(paramStr)) {
            return null;
        }

        String httpPkg;
        try {
            if (this.param.isUpPhoto) {
                // 上传图片
                httpPkg = HttpUtil.uploadPhoto(module, this.param.imgByte);

            } else if (this.param.isGetMethod) {
                // GET请求 (首页banner, 扫码转账)
                httpPkg = HttpUtil.sendGetRequestCallbackString(httpUrl + "?" + paramStr);

            } else {
                // POST请求
                byte[] paramBytes = paramStr.getBytes(Constants.CHARSET_UTF_8);
                httpPkg = sendRequest(httpUrl, paramBytes);
            }
        } catch (UnknownHostException e) {
            LogUtil.e(TAG, "~~~ConfigHelper.URL URL_Spare URL_Small_Three ---> can't use~~~~~");
            LogUtil.e(TAG, "UnknownHostException doAutoWapSend: " + e.toString());
            return onNetErrResp().toString();

        } catch (SocketTimeoutException e) {
            LogUtil.e(TAG, "SocketTimeoutException doAutoWapSend:" + e.toString());
            return onTimeOutResp().toString();

        } catch (HttpUtil.HttpStateException e) {
            LogUtil.e(TAG, "Exception doAutoWapSend:" + e.toString());
            httpPkg = onNetErrResp(e.getMessage()) != null ? onNetErrResp(e.getMessage()).toString() : "";

        } catch (Exception e) {
            LogUtil.i(TAG, "doAutoWapSend:" + e.toString());
            httpPkg = onNetErrResp().toString();
        }
        return httpPkg;
    }

    private String sendRequest(final String url, byte[] param) throws Exception {
        if (TextUtils.isEmpty(url) || param == null || param.length <= 0) {
            return null;
        }
        byte[] resultByte = HttpUtil.sendPostRequestCallbackByte(url, param);//请求
        String result = new String(resultByte);
        return result;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        LogUtil.i(TAG, "network state netConnect = " + netConnect);
        if (!netConnect) {
            LogUtil.d(TAG, "--Internet not connect--");
            param.rspJson = null;
            param.rspErr = onNetErrResp();
            return null;
        }

        try {
            String jsonRequestParam = param.req.execute();
            String body = doAutoWapSend(param.module, jsonRequestParam);
            JSONObject json = new JSONObject(body);
            String retCode = json.optString(Constants.RET_CODE_KEY);
            if (TextUtils.isEmpty(body)) {
                param.rspErr = onNetErrResp();

            } else if (TextUtils.equals(retCode, Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION)) {
                param.rspErr = json;

            } else {
                param.rspJson = json;
            }
        } catch (ClientException e) {
            LogUtil.e(TAG, "ClientException: " + e.toString());

        } catch (Exception e) {
            LogUtil.e(TAG, "doInBackground Exception:" + e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (param != null && param.listener != null) {
            if (param.rspJson != null) {
                String code = param.rspJson.optString(Constants.RET_CODE_KEY);
                String msg = param.rspJson.optString(Constants.MSG_KEY);
                LogUtil.d(TAG, "retCode: " + code);
                if (Constants.RET_CODE_W000.equals(code)
                        || Constants.RET_CODE_PP003.equals(code)
                        || Constants.RET_CODE_PP113.equals(code)) {
                    onBackLogin(msg);
                } else {
                    param.listener.onPostExecute(param.rspJson);
                }
            } else if (param.rspErr != null) {
                param.listener.onError(param.rspErr);
            } else {
                param.rspJson = null;
                param.listener.onError(onNetErrResp());
            }
        }
    }


    public static class Param {

        public String module;
        public Request req;
        public HttpReqTaskListener listener;
        public JSONObject rspJson;
        public JSONObject rspErr;
        public boolean isGetMethod;
        public boolean isUpPhoto;
        public byte[] imgByte;

        public Param(String module, Request req, HttpReqTaskListener listener) {
            this.module = module;
            this.req = req;
            this.listener = listener;
        }
    }

    private JSONObject onNetErrResp() {
        return onNetErrResp("");
    }

    /**
     * 网络连接失败
     * 没有网络
     */
    private JSONObject onNetErrResp(String state) {
        JSONObject jsonErr = null;
        try {
            jsonErr = new JSONObject();
            jsonErr.put(Constants.RET_CODE_KEY, Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION);
            String msg = null;
            GlobalApplication application = GlobalApplication.getApplication();
            if (application != null) {
                msg = application.getString(R.string.LocalError_C0);
            }
            if (!TextUtils.isEmpty(state)) {
                msg = msg + "( " + state + " )";
            }
            jsonErr.put(Constants.MSG_KEY, msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonErr;
    }

    /**
     * 网络异常,超时，没有应答包 等
     */
    private JSONObject onTimeOutResp() {
        JSONObject jsonErr = null;
        try {
            jsonErr = new JSONObject();
            jsonErr.put(Constants.RET_CODE_KEY, Constants.LOCAL_RET_CODE_NETWORK_EXCEPTION);
            String msg = "";
            if (AppGlobalUtil.getInstance().getContext() != null) {
                msg = AppGlobalUtil.getInstance().getContext().getString(R.string.LocalError_B0);
            }
            jsonErr.put(Constants.MSG_KEY, msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonErr;
    }

    /**
     * 返回到登录界面
     */
    private void onBackLogin(String errMsg) {
        ToolUtils.sendBroadcastBackLogin(errMsg);
    }
}

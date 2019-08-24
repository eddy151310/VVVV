package com.ahdi.wallet.app.main;

import android.content.Context;
import android.text.TextUtils;

import com.ahdi.wallet.R;
import com.ahdi.wallet.app.HttpReqApp;
import com.ahdi.wallet.app.callback.MsgSdkCallBack;
import com.ahdi.wallet.app.listener.msg.MsgListListener;
import com.ahdi.wallet.app.request.MsgListReq;
import com.ahdi.lib.utils.config.Constants;

import org.json.JSONObject;

/**
 * Date: 2018/1/31 下午2:30
 * Author: kay lau
 * Description:
 */
public class MessageSdkMain {

    private static MessageSdkMain instance;

    public String default_error = "";

    public MsgSdkCallBack callBack;

    private MessageSdkMain() {
    }

    public static MessageSdkMain getInstance() {
        if (instance == null) {
            synchronized (MessageSdkMain.class) {
                if (instance == null) {
                    instance = new MessageSdkMain();
                }
            }
        }
        return instance;
    }

    /**
     * 获取个人通知消息homeUrl
     *
     * @param context
     * @param sid
     */
    public void getMsgHomeUrl(Context context, String sid) {
        initDefaultError(context);
        MsgListReq request = new MsgListReq(sid);
        HttpReqApp.getInstance().onMsgList(request, new MsgListListener());
    }


    /**
     * 初始化默认的错误原因
     *
     * @param context
     */
    private void initDefaultError(Context context) {
        if (context != null) {
            default_error = context.getString(R.string.LocalError_C0);
        }
    }

    /**
     * 统一对外回调
     *
     * @param code       “0”成功 其他失败
     * @param errorMsg   错误描述
     * @param jsonObject 响应成功时的json数据
     */
    public void onResultBack(String code, String errorMsg, JSONObject jsonObject) {
        if (TextUtils.isEmpty(errorMsg)) {
            errorMsg = jsonObject == null ? "" : jsonObject.optString(Constants.MSG_KEY);
        }
        if (callBack != null) {
            callBack.onResult(code, errorMsg, jsonObject);
            onDestroy();
        }
    }

    /**
     * 清理
     */
    public void onDestroy() {
        callBack = null;
        instance = null;
    }
}

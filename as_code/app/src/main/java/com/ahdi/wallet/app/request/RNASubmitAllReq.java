package com.ahdi.wallet.app.request;

import android.text.TextUtils;

import com.ahdi.wallet.network.framwork.Request;
import com.ahdi.wallet.app.schemas.RnaExtraInfoSchema;
import com.ahdi.wallet.app.schemas.RnaInfoSchema;
import com.ahdi.lib.utils.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhaohe
 */
public class RNASubmitAllReq extends Request {

    private static final String TAG = RNASubmitAllReq.class.getSimpleName();

    //    字段名	        重要性	类型	                    描述
    //    RNAInfo	    必须	    RNAInfo_Schema          护照或临时居留证实名认证信息
    //    ImgID	        必须	    String	                证件图片
    //    ImgHandheld	必须	    String	                手持证件图片
    //    Type	        必须	    String	                实名认证类型：Passport、或KITAS、或KTP
    //    ExtraInfo	    条件	    RNAExtraInfo_Schema T   ype为KTP时此参数必选

    private RnaInfoSchema rnaInfoSchema;
    private String ImageID = "";
    private String imgHandheld = "";
    private String type = "";
    private RnaExtraInfoSchema rnaExtraInfoSchema = null;

    public RNASubmitAllReq(RnaInfoSchema rnaInfoSchema, String ImageID, String imgHandheID, String type, RnaExtraInfoSchema rnaExtraInfoSchema, String sid) {
        super(sid);
        this.rnaInfoSchema = rnaInfoSchema;
        this.ImageID = ImageID;
        this.imgHandheld = imgHandheID;
        this.type = type;
        this.rnaExtraInfoSchema = rnaExtraInfoSchema;
    }

    @Override
    protected JSONObject bodyWriteTo(JSONObject json) {
        if (json == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        try {
            if (rnaInfoSchema != null) {
                rnaInfoSchema.writeTo(body);
            }
            if (!TextUtils.isEmpty(type)) {
                body.put("Type", type);
            }
            if (!TextUtils.isEmpty(ImageID)) {
                body.put("ImgID", ImageID);
            }
            if (!TextUtils.isEmpty(imgHandheld)) {
                body.put("ImgHandheld", imgHandheld);
            }
            if (rnaExtraInfoSchema != null) {
                rnaExtraInfoSchema.writeTo(body);
            }
            json.put(BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, TAG + json.toString());
        return json;
    }

    @Override
    protected JSONObject getContentJson() {
        return null;
    }
}

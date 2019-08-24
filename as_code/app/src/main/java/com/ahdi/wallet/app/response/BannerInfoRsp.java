package com.ahdi.wallet.app.response;

import com.ahdi.wallet.network.framwork.ABSIO;
import com.ahdi.wallet.network.framwork.Response;
import com.ahdi.wallet.app.schemas.BannerSchema;

import org.json.JSONObject;

/**
 * banner信息结果响应
 *
 * @author zhaohe
 */
public class BannerInfoRsp extends Response {
//    Result	必须	Map<Integer,Map<String,Object>>
// 结果：返回该参数位置下对应banner信息，Integer为展示位。
//    值Map类型对应展示位中的各小位置内容。
//    More： String，点击更多时，url地址.
//    Banner：Banner_Schema[]

    public String more;
    public BannerSchema[] scrollBanners;
    public BannerSchema[] staticBanners;

    @Override
    public void bodyReadFrom(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject body = json.optJSONObject(data);
        if (body == null) {
            return;
        }

        JSONObject jsonResult = body.optJSONObject("Result");

        JSONObject jsonPositionOne = jsonResult.optJSONObject("1");
        if (jsonPositionOne != null) {
            more = jsonPositionOne.optString("More");
            scrollBanners = ABSIO.decodeSchemaArray(BannerSchema.class, "Banner", jsonPositionOne);
        }

        JSONObject jsonPositionTwo = jsonResult.optJSONObject("2");
        if (jsonPositionOne != null) {
            staticBanners = ABSIO.decodeSchemaArray(BannerSchema.class, "Banner", jsonPositionTwo);
        }
    }

}

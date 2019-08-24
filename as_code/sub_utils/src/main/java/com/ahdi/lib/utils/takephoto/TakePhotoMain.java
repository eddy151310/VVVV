package com.ahdi.lib.utils.takephoto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.takephoto.activity.PhotoMangerActivity;
import com.ahdi.lib.utils.takephoto.callback.TakePhotoCallBack;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/5/17.
 *
 * 拍照入口
 */

public class TakePhotoMain {
    private static final String TAG = "TakePhotoMain";

    private static TakePhotoMain instance = null;

    public final static String RESULT_SUCCESS = "PHOTO_SUCCESS";
    public final static String RESULT_FAIL = "PHOTO_FAIL";
    public final static String RESULT_CANCEL = "PHOTO_CANCEL";
    /**
     * 采集图片类型位身份证
     */
    public static final int TYPE_ID_CARD = 10;
    /**
     * 采集护照
     */
    public static final int TYPE_PASSPORT_CARD = 11;
    /**
     * 采集kitas
     */
    public static final int TYPE_KITAS_CARD = 12;

    /**
     * 采集用户手持身份拍照
     */
    public static final int TYPE_HOLD_ID_PHOTO = 20;
    /**
     * 采集用户手持护照拍照
     */
    public static final int TYPE_HOLD_PASSPORT_PHOTO = 21;
    /**
     * 采集用户手持居住证拍照
     */
    public static final int TYPE_HOLD_KITAS_PHOTO = 21;


    /**
     * 采集用户头像拍照
     */
    public static final int TYPE_USER_PHOTO_CAMERA = 30;
    /**
     * 采集用户头像相册
     */
    public static final int TYPE_USER_PHOTO_ALBUM = 40;
    /**
     * 上次点击时间
     */
    private static long lastClickTime;
    private TakePhotoCallBack callBack = null;
    /**
     * 拍照之后的bitmap
     */
    public Bitmap bitmap = null;
    /**
     * 当前的拍照类型
     */
    public int currentType = TYPE_USER_PHOTO_CAMERA;

    private TakePhotoMain() {
    }

    public static TakePhotoMain getInstance() {
        if (instance == null){
            synchronized (TakePhotoMain.class){
                if (instance == null){
                    instance = new TakePhotoMain();
                }
            }
        }
        return instance;
    }

    /**
     * 拍照接口
     * @param context
     * @param photoType
     * @param callBack
     */
    public void takePhoto(Context context, int photoType, TakePhotoCallBack callBack){
        if(!checkContextAndCallBack(context, callBack)){
            return;
        }
        if (photoType < 0){
            LogUtil.e(TAG, "参数错误：请输入正确的photoType");
            callBack.onResult(RESULT_FAIL, photoType, "参数错误：请输入正确的photoType" ,null, "");
        }
        if (!isCanLoading()) {
            LogUtil.e(TAG, "Please do not repeat submit");
            return;
        }
        this.callBack = callBack;
        currentType = photoType;
        openPhotoManagerActivity(context);
    }

    /**
     *  图片管理activity
     * @param context
     */
    private void openPhotoManagerActivity(Context context){
        try{
            Intent intent = new Intent(context, PhotoMangerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }catch (Exception ex){
            ex.printStackTrace();
            LogUtil.e(TAG, ex.getMessage());
        }
    }



    /**
     * 统一对外回调
     * @param Code 结果（成功，失败，取消）
     * @param reason 结果描述
     * @param resultBitmap 处理完成之后的图片（可能为空）
     * @param photoPath 成功时返回图片保存的绝对路径（可能为空）
     */
    public void onResult(String Code, String reason, Bitmap resultBitmap,String photoPath){
        if (callBack != null){
            callBack.onResult(Code, currentType, reason, resultBitmap, photoPath);
            callBack = null;
        }
        bitmap = null;
        instance = null;
    }

    /**
     * 检查context 和 callback
     *
     * @param context
     * @param callBack
     * @return
     */
    private static boolean checkContextAndCallBack(Context context, TakePhotoCallBack callBack) {

        if (context == null) {
            LogUtil.d(TAG, "context can't be empty");
            return false;
        }
        if (callBack == null) {
            LogUtil.d(TAG, "callback can't be empty");
            return false;
        }
        return true;
    }

    /**
     * 是否可以再次加载 防止多次调用接口
     *
     * @return
     */
    private static boolean isCanLoading() {

        try {
            long time = System.currentTimeMillis();
            long offSetTime = time - lastClickTime;
            if (Math.abs(offSetTime) > 500) {
                lastClickTime = time;
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}

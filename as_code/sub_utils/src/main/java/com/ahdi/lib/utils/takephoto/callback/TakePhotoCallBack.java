package com.ahdi.lib.utils.takephoto.callback;

import android.graphics.Bitmap;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/5/17.
 */

public interface TakePhotoCallBack {
    /**
     * 拍照的结果回调
     * @param Code 结果（成功，失败，取消）
     * @param type 拍照的类型
     * @param reason 结果描述
     * @param resultBitmap 处理完成之后的图片（可能为空）
     * @param photoPath 成功时返回图片保存的绝对路径（可能为空）
     */
    void onResult(String Code, int type, String reason, Bitmap resultBitmap, String photoPath);
}

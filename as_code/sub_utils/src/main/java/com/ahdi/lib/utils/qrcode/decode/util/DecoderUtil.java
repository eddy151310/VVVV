package com.ahdi.lib.utils.qrcode.decode.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ahdi.lib.utils.utils.LogUtil;

public class DecoderUtil {



    /**
     * 将本地图片文件转换成可解码二维码的 Bitmap。为了避免图片太大，这里对图片进行了压缩。感谢 https://github.com/devilsen 提的 PR
     * @param picturePath 本地图片文件路径
     * @return
     */
    public static Bitmap getLocalBitmap(String picturePath) {

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            BitmapFactory.decodeFile(picturePath, options);

            float realWidth = options.outWidth;
            float realHeight = options.outHeight;
            LogUtil.d("", "========真实图片高度：" + realHeight + "宽度:" + realWidth);

            int scale = 1;
            if (realWidth > 400f) {
                scale = (int) (realWidth / 400f);
            } else if (realHeight > 640f) {
                scale = (int) (realHeight / 640f);
            }

            LogUtil.d("", "---------options.inSampleSize -- scale：" + scale);

            if (scale <= 2) {
                scale = 1;
            } else if (scale > 2 && scale <= 4) {
                scale = 2;
            } else if (scale > 4 && scale <= 8) {
                scale = 4;
            }else{
                scale = 8 ;
            }

            options.inSampleSize = scale;

            options.inJustDecodeBounds = false;
            //如果将这个值置为true，那么在解码的时候将不会返回bitmap，只会返回这个bitmap的尺寸。这个属性的目的是，如果你只想知道一个bitmap的尺寸，但又不想将其加载到内存时。这是一个非常有用的属性。

            Bitmap mBitmap = (BitmapFactory.decodeFile(picturePath, options));
            float realWidth2 = mBitmap.getWidth();
            float realHeight2 = mBitmap.getHeight();
            LogUtil.d("", "---------缩略图片高度：" + realHeight2 + "宽度:" + realWidth2);
            return mBitmap;

        } catch (Exception e) {
            LogUtil.d("", "Bitmap 转换失败 :" + e.toString());
            return null;
        }
    }




    /**
     * 回收Bitmap
     * @param bitmap
     */
    public static void gcBitmap(Bitmap bitmap) {
        try {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            System.gc();
        } catch (Exception e) {
            e.toString();
        }
    }




    public static byte[] getYUV420sp(int inputWidth, int inputHeight, Bitmap scaled) {
        int[] argb = new int[inputWidth * inputHeight];
        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);
        byte[] yuv = new byte[inputWidth * inputHeight];
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);
        DecoderUtil.gcBitmap(scaled);
        return yuv;
    }


    private static void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        // 帧图片的像素大小
        final int frameSize = width * height;
        // ---YUV数据---
        int Y, U, V;
        // Y的index从0开始
        int yIndex = 0;
        // UV的index从frameSize开始
        int uvIndex = frameSize;

        // ---颜色数据---
//      int a, R, G, B;
        int R, G, B;
        //
        int argbIndex = 0;
        //

        // ---循环所有像素点，RGB转YUV---
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                // a is not used obviously
//              a = (argb[argbIndex] & 0xff000000) >> 24;
                R = (argb[argbIndex] & 0xff0000) >> 16;
                G = (argb[argbIndex] & 0xff00) >> 8;
                B = (argb[argbIndex] & 0xff);
                //
                argbIndex++;

                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                //
                Y = Math.max(0, Math.min(Y, 255));
                U = Math.max(0, Math.min(U, 255));
                V = Math.max(0, Math.min(V, 255));

                // NV21 has a plane of Y and interleaved planes of VU each
                // sampled by a factor of 2
                // meaning for every 4 Y pixels there are 1 V and 1 U. Note the
                // sampling is every other
                // pixel AND every other scanline.
                // ---Y---
                yuv420sp[yIndex++] = (byte) Y;

                // ---UV---
//              if ((j % 2 == 0) && (i % 2 == 0)) {
//
//
//
//                  yuv420sp[uvIndex++] = (byte) V;
//
//                  yuv420sp[uvIndex++] = (byte) U;
//              }
            }
        }
    }
}

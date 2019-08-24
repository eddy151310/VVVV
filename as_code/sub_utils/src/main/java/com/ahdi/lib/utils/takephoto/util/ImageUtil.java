package com.ahdi.lib.utils.takephoto.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.ToastUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {

    private final static String TAG = ImageUtil.class.getSimpleName();

    public static final int MAX_TWO_M = 2048 * 1024;

    /**
     * 处理 图片
     *
     * @param context
     * @param path
     * @param toSize  如果获取到的图片大于toSize,则缩小。
     */
    public static Bitmap resizerBitmap(Context context, String path, int toSize) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        if (!path.endsWith(".jpg") && !path.endsWith(".jpeg") && !path.endsWith(".bmp") && !path.endsWith(".png")) {
            LogUtil.d(TAG, "不支持的图片格式");
            ToastUtil.showToastShort(context, "Only support format‘bmp、jpeg、jpg、png’picture");
            return null;
        }
        int degree = readPictureDegree(path);
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, toSize);
            options.inJustDecodeBounds = false;
            Bitmap toBitmap = BitmapFactory.decodeFile(path, options);
            if (degree != 0) {
                toBitmap = rotateBitmap(degree, toBitmap);
            }
            return toBitmap;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    /**
     * 计算SampleSize
     *
     * @param oriWidth
     * @param oriHeight
     * @param toSize
     * @return
     */
    private static int calculateSampleSize(int oriWidth, int oriHeight, int toSize) {
        int inSampleSize = 1;
        int toWidth = oriWidth;
        int toHeight = oriHeight;
        while (toWidth * toHeight > toSize) {
            inSampleSize *= 2;
            toWidth = oriWidth / inSampleSize;
            toHeight = oriHeight / inSampleSize;
        }
        return inSampleSize;
    }

    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle  被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmap(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵  
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片  
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }

    /**
     * 等比例缩放
     *
     * @param bm
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scale = (float) newWidth / width;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        bm.recycle();
        return newbm;
    }

    /**
     * bitmap 转成 String
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToString(Bitmap bitmap) {

        String result = "";
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                byte[] bitmapBytes = baos.toByteArray();
                result = android.util.Base64.encodeToString(bitmapBytes, android.util.Base64.URL_SAFE | android.util.Base64.NO_WRAP);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * bitmap 转成 byte
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToByte(Bitmap bitmap) {

        byte[] result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                result = baos.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 裁剪出特定区域的图片
     *
     * @param context
     * @param data     图片原始数据
     * @param cropRect 裁剪区域
     * @return
     */
    public static Bitmap cropBitmap(Context context, byte[] data, Point point, Rect cropRect) {
        Bitmap idBitmap = null;
        if (context == null) {
            LogUtil.d(TAG, "裁剪图片时，context == null");
            return idBitmap;
        }
        if (data == null || data.length == 0) {
            LogUtil.d(TAG, "图片数据为空");
            return idBitmap;
        }
        try {
            Bitmap oriBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            int pic_width = oriBitmap.getWidth();//手机高度 1280
            int pic_height = oriBitmap.getHeight();//手机宽度720
            float widthScale = pic_width * 1.0f / (point.y);
            float heightScale = pic_height * 1.0f / point.x;
            float x_center, y_center;
            x_center = pic_width * 1.0f / 2;
            y_center = pic_height * 1.0f / 2;
            float x = cropRect.top * widthScale;
            float y = (point.x - cropRect.right) * heightScale;
            float width = cropRect.height() * widthScale;
            float height = cropRect.width() * heightScale;
            LogUtil.d(TAG, "cropRect width =  " + cropRect.height() + "height = " + cropRect.width());
            Matrix matrix = new Matrix();
            matrix.postRotate(360, x_center, y_center);
            idBitmap = Bitmap.createBitmap(oriBitmap, (int) x, (int) y, (int) width, (int) height, matrix, false);
            oriBitmap.recycle();
            idBitmap = rotateBitmap(90, idBitmap);
            LogUtil.d(TAG, "旋转之后图片width =  " + idBitmap.getWidth() + "height = " + idBitmap.getHeight());
            if (idBitmap.getWidth() > 4096) {
                idBitmap = scaleBitmap(idBitmap, 4096, 4096);
            }
            LogUtil.d(TAG, "scaleBitmap之后图片width =  " + idBitmap.getWidth() + "height = " + idBitmap.getHeight());
            idBitmap = compressBitmap(idBitmap, 256, 200 * 1024);
            LogUtil.d(TAG, "compressBitmap之后图片width =  " + idBitmap.getWidth() + "height = " + idBitmap.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
            return idBitmap;
        }
        return idBitmap;
    }

    /**
     * 压缩图片质量   尺寸大于256*256 小于 4096*4096 小于2M。为了减轻服务端的压力尽量控制在200Kb左右
     *
     * @param image
     * @param minHeight 最小高度
     * @param maxSize   最大200kb  200*1024
     * @return
     */
    public static Bitmap compressBitmap(Bitmap image, int minHeight, int maxSize) {
        LogUtil.d(TAG, "压缩前：width = " + image.getWidth() + ",height = " + image.getHeight());

        int compressScale = 1;
        if (image.getByteCount() > maxSize) {
            compressScale = (int) Math.sqrt(image.getByteCount() / maxSize);
        }
        if (compressScale <= 1) {
            compressScale = 1;
        } else {
            while (image.getHeight() < compressScale * minHeight) {
                compressScale--;
                if (compressScale <= 1) {
                    compressScale = 1;
                    break;
                }
            }
        }
        LogUtil.d(TAG, "compressScale = " + compressScale);
        if (compressScale == 1) {
            return image;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inSampleSize = compressScale;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        try {
            os.close();
            image.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG, "压缩后：width = " + bitmap.getWidth() + ",height = " + bitmap.getHeight());
        return bitmap;
    }

    public static void saveBitmap(Bitmap oriBitmap, File file) {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            /* 采用压缩转档方法 */
            oriBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            /* 调用flush()方法，更新BufferStream */
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片的存储路径
     *
     * @return
     */
    public static File getImagePath(Context context, String fileName) {
        if (context == null) {
            return null;
        }
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File SDFile = android.os.Environment.getExternalStorageDirectory();
                File folder = new File(SDFile.getAbsolutePath() + File.separator + ConfigCountry.PLATFORMID_FOLDER);
                if (!folder.exists()) {
                    folder.mkdir();
                }
                File picDir = new File(SDFile.getAbsolutePath() + File.separator + ConfigCountry.PLATFORMID_FOLDER + File.separator + "pic");
                if (!picDir.exists()) {
                    picDir.mkdir();
                }
                return new File(picDir, fileName);
            } else {
                return new File(context.getCacheDir(), fileName);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}


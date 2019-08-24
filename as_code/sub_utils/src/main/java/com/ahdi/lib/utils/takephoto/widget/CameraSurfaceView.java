package com.ahdi.lib.utils.takephoto.widget;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.ahdi.lib.utils.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 *
 * https://www.jianshu.com/p/7f766eb2f4e7
 * https://github.com/tianyalian/CropperCammer
 */

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    private static final int MIN_PREVIEW_PIXELS = 480 * 640;
    private static final double MAX_ASPECT_DISTORTION = 0.3;
    private double MAX_PREVIEW_PIXELS ;

    private static final String TAG = "CameraSurfaceView";

    public CameraSurfaceView(Context context) {
        this(context,null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private int mScreenWidth;
    private int mScreenHeight;
    private TakePic listener;
    private boolean isTakingPhoto = false;
    /***预览图的大小*/
    private Point prePoint = null;

    public boolean isTakingPhoto() {
        return isTakingPhoto;
    }

    public void setTakingPhoto(boolean takingPhoto) {
        isTakingPhoto = takingPhoto;
    }

    public void setListener(TakePic listener) {
        this.listener = listener;
    }

    /**
     * 初始化
     */
    private void initView(Context context) {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        getScreenMetric(context);
        MAX_PREVIEW_PIXELS = mScreenWidth * mScreenHeight;
    }

    private void getScreenMetric(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = WM.getDefaultDisplay().getWidth();
        mScreenHeight = WM.getDefaultDisplay().getHeight();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.d(TAG,"****************surfaceCreated");
        if (mCamera == null){
            try {
                mCamera = Camera.open();//后置摄像头
                initFromCameraParameters(mCamera, mScreenWidth, mScreenHeight);
                mCamera.setPreviewDisplay(holder);
            } catch (Exception e) {
                e.printStackTrace();
                if (listener != null){
                    listener.cameraError(e.getMessage());
                }
            }
        }
    }

    public void initFromCameraParameters(Camera camera, int width, int height) {
        LogUtil.i(TAG,"setCameraParams  width="+width+"  height="+height + ",rate = " + height*1.0d/width);
        Camera.Parameters parameters = mCamera.getParameters();
//        /**从列表中选取合适的图片分辨率*/
//        Point picSize = getProperSize(false, parameters,height ,width);
//        if (null != picSize) {
//            LogUtil.i(TAG, "picSize.width=" + picSize.x + "  picSize.height=" + picSize.y);
//            parameters.setPictureSize(picSize.x,picSize.y);
//        }
        /**从列表中选取合适的预览分辨率*/
        prePoint = getProperSize(true, parameters, height, width);
        if (null != prePoint) {
            LogUtil.i(TAG, "preSize.width=" + prePoint.x + "  preSize.height=" + prePoint.y);
            parameters.setPreviewSize(prePoint.x, prePoint.y);
        }

        parameters.setJpegQuality(100); // 设置照片质量
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        }
        mCamera.setDisplayOrientation(90);// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
//        //设置图片格式
        parameters.setPictureFormat(ImageFormat.JPEG);
//        //设置图片的质量
        parameters.set("jpeg-quality", 100);
        mCamera.setParameters(parameters);
    }

    /**
     * 获取合适的预览图和图片大小
     * @param isPre true 是预览图
     * @param parameters
     * @param height
     * @param width
     * @return
     */
    private Point getProperSize(boolean isPre, Camera.Parameters parameters, int height, int width) {
        List<Camera.Size> rawSupportedSizes = null;
        if (isPre){
            rawSupportedSizes = parameters.getSupportedPreviewSizes();
        }else {
            rawSupportedSizes = parameters.getSupportedPictureSizes();
        }

        if (rawSupportedSizes == null) {
            Camera.Size defaultSize = null;
            if (isPre){
                defaultSize = parameters.getPreviewSize();
            }else {
                defaultSize = parameters.getPictureSize();
            }
            return new Point(defaultSize.width, defaultSize.height);
        }

        // Sort by size, descending
        List<Camera.Size> supportedPreviewSizes = new ArrayList<Camera.Size>(rawSupportedSizes);
        Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });

        double screenAspectRatio = (double) height / (double) width;

        // Remove sizes that are unsuitable
        Iterator<Camera.Size> it = supportedPreviewSizes.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewSize = it.next();

            int realWidth = supportedPreviewSize.width;
            int realHeight = supportedPreviewSize.height;
            if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {  //过滤掉 小于 480 * 320;
                it.remove();
                continue;
            }
            if (realWidth * realHeight > MAX_PREVIEW_PIXELS && width != realHeight) {  //过滤掉 大于屏幕尺寸的
                it.remove();
                continue;
            }

            boolean isCandidatePortrait = realWidth < realHeight;
            int maybeFlippedWidth = isCandidatePortrait ? realHeight : realWidth;
            int maybeFlippedHeight = isCandidatePortrait ? realWidth : realHeight;

            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);

            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
                continue;
            }
            LogUtil.d(TAG, "筛选之后的尺寸： width = " + maybeFlippedWidth + ", height = " + maybeFlippedHeight + ",aspectRatio = " + aspectRatio);

            if (maybeFlippedWidth == height && maybeFlippedHeight == width) {
                Point exactPoint = new Point(maybeFlippedWidth, maybeFlippedHeight);
                return exactPoint;
            }
        }

        if (!supportedPreviewSizes.isEmpty()) {
            Camera.Size largestPreview = supportedPreviewSizes.get(0);
            Point largestSize = new Point(largestPreview.width, largestPreview.height);
            return largestSize;
        }

        // If there is nothing at all suitable, return current preview size
        Camera.Size defaultPreview = null;
        if (isPre) {
            defaultPreview = parameters.getPreviewSize();
        }else {
            defaultPreview = parameters.getPictureSize();
        }
        Point defaultSize = new Point(defaultPreview.width, defaultPreview.height);
        return defaultSize;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        LogUtil.d(TAG,"****************surfaceChanged");
        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
            if (listener != null){
                listener.cameraError(e.getMessage());
            }
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e){
            LogUtil.d(TAG, "Error starting mCamera preview: " + e.getMessage());
            if (listener != null){
                listener.cameraError(e.getMessage());
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.d(TAG,"****************surfaceChanged");
        if (mCamera != null){
            mCamera.cancelAutoFocus();
            mCamera.stopPreview();
            mCamera.release();
        }
        mCamera = null;
        holder = null;
    }

    /**
     *对外提供的拍照方法
     */
    public void takePicture() {
        if (isTakingPhoto){
            return;
        }
        if (mCamera == null){
            return;
        }
        isTakingPhoto = true;
        try {
            mCamera.autoFocus(autoFocusCallback);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void onResume(){
        isTakingPhoto = false;
        if (mCamera != null){
            mCamera.startPreview();
        }
    }

    public void onPause(){
        if (mCamera != null){
            mCamera.stopPreview();
        }
    }

    /**
     * 拍照回调接口
     */
    public interface TakePic{
        /**
         * 照片数据
         * @param data
         */
        void onTakePic(byte[] data);

        /**
         * camera 出现异常
         * @param errorMsg
         */
        void cameraError(String errorMsg);
    }

    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (mCamera != null){
//                mCamera.takePicture(null,null, new Camera.PictureCallback(){
//
//                    @Override
//                    public void onPictureTaken(byte[] data, Camera camera) {
//                        if (listener != null){
//                            listener.onTakePic(data);
//                        }
//                    }
//                });
                mCamera.setOneShotPreviewCallback(previewCallback);
            }
        }
    };
    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (!isTakingPhoto){
                return;
            }
            if (listener != null){
                listener.onTakePic(decodePreData(data));
            }
        }
    };

    private byte[] decodePreData(byte[] preData){
        byte[] tmp = null;
        if (prePoint == null){
            return null;
        }
        ByteArrayOutputStream os = null;
        try {
            YuvImage image = new YuvImage(preData, ImageFormat.NV21, prePoint.x, prePoint.y,null);
            os = new ByteArrayOutputStream(preData.length);
            if (!image.compressToJpeg(new Rect(0,0,prePoint.x, prePoint.y), 100, os)){
                return null;
            }
            tmp = os.toByteArray();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (os != null){
                os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tmp;
    }
}

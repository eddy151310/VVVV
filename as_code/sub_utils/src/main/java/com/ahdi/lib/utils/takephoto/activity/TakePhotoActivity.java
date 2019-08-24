package com.ahdi.lib.utils.takephoto.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahdi.lib.utils.R;
import com.ahdi.lib.utils.base.BaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.takephoto.TakePhotoMain;
import com.ahdi.lib.utils.takephoto.util.ImageUtil;
import com.ahdi.lib.utils.takephoto.widget.CameraSurfaceView;
import com.ahdi.lib.utils.takephoto.widget.IDViewFinder;
import com.ahdi.lib.utils.widgets.ToastUtil;

/**
 * @author admin
 */
public class TakePhotoActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = TakePhotoActivity.class.getSimpleName();

    private View scanCropView;
    /**
     * 标题栏关闭按钮，拍照按钮
     */
    private ImageView iv_title_close, iv_camera_take_pic;
    /**
     * 照片预览区域
     */
    private CameraSurfaceView cameraSurfaceView;
    private IDViewFinder idViewFinder;
    private TextView tv_take_photo_tip;

    private final int RESULT_ACTIVITY_REQUEST_CODE = 12;
    /**
     * 关闭页面的结果码
     */
    public static final int RESULT_CLOSE_CODE = 20;
    /**
     * SurfaceView延迟显示
     */
    private static final int DELAY_TIME = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_take_photo_layout);
        initViews();
    }

    private void initViews() {
        scanCropView = findViewById(R.id.capture_crop_view);
        iv_title_close =  findViewById(R.id.iv_title_close);
        iv_title_close.setOnClickListener(this);

        tv_take_photo_tip =  findViewById(R.id.tv_take_photo_tip);
        int currentType = TakePhotoMain.getInstance().currentType;
        if (currentType == TakePhotoMain.TYPE_ID_CARD){
            tv_take_photo_tip.setText(R.string.IdTakePhoto_A0);
        }else if (currentType == TakePhotoMain.TYPE_PASSPORT_CARD){
            tv_take_photo_tip.setText(R.string.IdTakePhoto_C0);
        }else if (currentType == TakePhotoMain.TYPE_KITAS_CARD){
            tv_take_photo_tip.setText(R.string.IdTakePhoto_B0);
        }

        iv_camera_take_pic =  findViewById(R.id.camera_take_pic);
        iv_camera_take_pic.setOnClickListener(this);
        idViewFinder = findViewById(R.id.idViewFinder);
        cameraSurfaceView = findViewById(R.id.camera_view);
        cameraSurfaceView.setOnClickListener(this);
        cameraSurfaceView.setListener(new CameraSurfaceView.TakePic(){

            @Override
            public void onTakePic(byte[] data) {
                cameraSurfaceView.setTakingPhoto(false);
                if (data != null && data.length > 0){
                    Bitmap bitmap = ImageUtil.cropBitmap(TakePhotoActivity.this, data, getCameraSize(), initCrop());
                    if (bitmap != null){
                        TakePhotoMain.getInstance().bitmap = bitmap;
                        Intent intent = new Intent(TakePhotoActivity.this, TakePhotoResultActivity.class);
                        startActivityForResult(intent, RESULT_ACTIVITY_REQUEST_CODE);
                    }else{
                        cameraSurfaceView.onResume();
                        LogUtil.d(TAG,"图片截取失败");
                    }
                }else{
                    cameraSurfaceView.onResume();
                    LogUtil.d(TAG,"拍照失败，数据为空");
                }

            }

            @Override
            public void cameraError(String msg) {
               LogUtil.d(TAG, "摄像头处理失败，" + msg);
                ToastUtil.showToastShort(TakePhotoActivity.this, getString(R.string.Toast_B0).concat(Constants.LOCAL_CAMERA_ERROR));
            }
        } );
        idViewFinder.postDelayed(new Runnable() {
            @Override
            public void run() {
                idViewFinder.setRect(initCrop());
                cameraSurfaceView.setVisibility(View.VISIBLE);
                getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
            }
        }, DELAY_TIME);
    }


    @Override
    public void onClick(View v) {
        if (!isCanClick()){
            return;
        }
        int id = v.getId();
        if (id == R.id.camera_take_pic) {
            takePic();
        }else if (id == R.id.iv_title_close) {
            TakePhotoMain.getInstance().onResult(TakePhotoMain.RESULT_CANCEL, "主动关闭", null, "");
            finish();
        }
    }

    /**
     * 拍照
     */
    public void takePic(){
        if (checkCameraHardware()){
            cameraSurfaceView.takePicture();
        }else{
            LogUtil.d(TAG,"没有找到可用的摄像头");
        }
    }

    /**
     * 检查是否有可用的摄像头
     * @return
     */
    private boolean checkCameraHardware() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }

    //region  初始化截取的矩形区域
    public Rect initCrop() {
        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);
        int cropLeft = location[0];
        int cropTop = location[1];
        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();
        return new Rect(cropLeft, cropTop, cropWidth + cropLeft, cropHeight + cropTop);
    }

    private Point getCameraSize() {
        Point point = new Point(cameraSurfaceView.getWidth(), cameraSurfaceView.getHeight());
        return point;
    }

    @Override
    public void onBackPressed() {
        TakePhotoMain.getInstance().onResult(TakePhotoMain.RESULT_CANCEL, "主动关闭", null, "");
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSurfaceView.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_CLOSE_CODE){
            finish();
        }
    }
}

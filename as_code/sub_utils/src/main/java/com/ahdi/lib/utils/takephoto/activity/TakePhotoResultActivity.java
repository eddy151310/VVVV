package com.ahdi.lib.utils.takephoto.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahdi.lib.utils.R;
import com.ahdi.lib.utils.base.BaseActivity;
import com.ahdi.lib.utils.takephoto.TakePhotoMain;
import com.ahdi.lib.utils.takephoto.util.ImageUtil;
import com.ahdi.lib.utils.takephoto.widget.ClipImageView;
import com.ahdi.lib.utils.utils.DateUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author admin
 */
public class TakePhotoResultActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = TakePhotoResultActivity.class.getSimpleName();
    public static final String PHOTO_PATH_KEY = "PHOTO_PATH_KEY";
    /**展示图片*/
    public static final int TYPE_SHOW_RESULT = 0;
    /**裁剪图片*/
    public static final int TYPE_CROP_IMAGE = 1;
    /**
     * 拍照结果展示区域
     * 标题栏关闭按钮，拍照按钮
     */
    private RelativeLayout rl_id_area;
    private ImageView iv_photo;
    private TextView tv_retake,tv_confirm;
    /**
     * 图片裁剪区域
     */
    private RelativeLayout rl_crop_photo_area;
    private ClipImageView iv_source_photo;
    private View crop_view;
    /** 展示结果或者裁剪图片*/
    private int currentType;
    /**传入的图片地址*/
    private String sourcePhotoPath = "";

    private LoadingDialog loadingDialog = null;
    private File clipImageFile = null;
    private Bitmap resultBitmap = null;
    private int currentPhotoType = TakePhotoMain.getInstance().currentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_take_photo_result_layout);
        initIntent();
        initViews();
        initData();
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (intent != null){
            sourcePhotoPath = intent.getStringExtra(PHOTO_PATH_KEY);
        }
        if (!TextUtils.isEmpty(sourcePhotoPath)){
            currentType = TYPE_CROP_IMAGE;
        }else if (TakePhotoMain.getInstance().bitmap != null){
            currentType = TYPE_SHOW_RESULT;
        }else {
            finish();
        }
    }

    private void initViews() {
        //不同布局
        rl_id_area = findViewById(R.id.rl_id_area);
        rl_crop_photo_area = findViewById(R.id.rl_crop_photo_area);
        //底部 确定取消按钮
        tv_retake = (TextView) findViewById(R.id.tv_retake);
        tv_retake.setOnClickListener(this);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(this);
        //加载不同布局的view
        if (currentType == TYPE_SHOW_RESULT){
            rl_id_area.setVisibility(View.VISIBLE);
            rl_crop_photo_area.setVisibility(View.GONE);
            iv_photo = (ImageView) findViewById(R.id.iv_photo);
            tv_retake.setText(R.string.IdPhotoResult_A0);
            tv_confirm.setText(R.string.IdPhotoResult_B0);
        }else if(currentType == TYPE_CROP_IMAGE){
            rl_id_area.setVisibility(View.GONE);
            rl_crop_photo_area.setVisibility(View.VISIBLE);
            iv_source_photo = findViewById(R.id.iv_source_photo);
            int realType = TakePhotoMain.getInstance().currentType;
            if (realType == TakePhotoMain.TYPE_HOLD_ID_PHOTO ||
                    realType == TakePhotoMain.TYPE_HOLD_KITAS_PHOTO ||
                    realType == TakePhotoMain.TYPE_HOLD_PASSPORT_PHOTO ){
                crop_view = findViewById(R.id.crop_holding_photo_view);
            }else {
                crop_view = findViewById(R.id.crop_user_photo_view);
            }
            if (realType == TakePhotoMain.TYPE_USER_PHOTO_CAMERA || realType == TakePhotoMain.TYPE_USER_PHOTO_ALBUM){
                tv_retake.setText(R.string.UserPhotoCrop_A0);
                tv_confirm.setText(R.string.UserPhotoCrop_B0);
            }
            crop_view.setVisibility(View.VISIBLE);
            rl_crop_photo_area.post(new Runnable() {
                @Override
                public void run() {
                    iv_source_photo.updateBorder(initCrop());
                }
            });
        }
    }

    private void initData() {
        if (currentType == TYPE_SHOW_RESULT){
            showResultBitmap();
        }else if(currentType == TYPE_CROP_IMAGE){
            cropPhotoView();
        }
    }

    public Rect initCrop() {
        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        crop_view.getLocationInWindow(location);
        int cropLeft = location[0];
        int cropTop = location[1];
        int cropWidth = crop_view.getWidth();
        int cropHeight = crop_view.getHeight();
        return new Rect(cropLeft, cropTop, cropWidth + cropLeft, cropHeight + cropTop);
    }

    /**
     * 显示图片
     */
    private void showResultBitmap(){
        if (TakePhotoMain.getInstance().bitmap != null){
            iv_photo.setImageBitmap(TakePhotoMain.getInstance().bitmap);
        }else{
            LogUtil.e(TAG, "照片显示页面 TakePhotoMain.getInstance().bitmap = null");
            finish();
        }
    }

    /**
     * 裁剪图片
     */
    private void cropPhotoView(){
        if (!TextUtils.isEmpty(sourcePhotoPath)){
            Bitmap bitmap = ImageUtil.resizerBitmap(TakePhotoResultActivity.this, sourcePhotoPath, ImageUtil.MAX_TWO_M);
            if (bitmap != null){
                iv_source_photo.setImageBitmap(bitmap);
            }else {
                onResult(TakePhotoMain.RESULT_CANCEL, "加载图片失败", null, null);
                finish();
            }
        }else {
            onResult(TakePhotoMain.RESULT_CANCEL, "图片裁剪页图片路径为空", null, null);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (!isCanClick()){
            return;
        }
        int id = v.getId();
        if (id == R.id.tv_retake) {
            onCancel();
        }else if (id == R.id.tv_confirm) {
            onConfirm();
        }
    }

    /**
     * 确定按钮
     */
    private void onConfirm() {
        if (currentType == TYPE_SHOW_RESULT){
            onResult(TakePhotoMain.RESULT_SUCCESS, "", TakePhotoMain.getInstance().bitmap,"");
            setResult(TakePhotoActivity.RESULT_CLOSE_CODE);
            finish();
        }else if (currentType == TYPE_CROP_IMAGE){
            clipImage();
        }
    }

    /**
     * 关闭
     */
    private void onCancel() {
        if (currentType == TYPE_SHOW_RESULT){
            finish();
        }else if(currentType == TYPE_CROP_IMAGE){
            onResult(TakePhotoMain.RESULT_CANCEL,"主动关闭",null, "");
            finish();
        }
    }
    /**
     * 裁剪图片
     */
    private void clipImage() {
        loadingDialog = showLoading();
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Bitmap bitmap = iv_source_photo.clip();
                    resultBitmap = ImageUtil.compressBitmap(bitmap,360, 200*1024);
                    onResult(TakePhotoMain.RESULT_SUCCESS,null, resultBitmap, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, "裁剪图片失败：" + e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                closeLoading(loadingDialog);
                finish();
            }
        };
        task.execute();
    }

    /**
     * 保存bitmap
     */
    private void saveBitmap() {
        clipImageFile = ImageUtil.getImagePath(TakePhotoResultActivity.this, DateUtil.formatTimeDMYNoHMS(System.currentTimeMillis()) + ".jpg");
        if (clipImageFile != null) {
            FileOutputStream fos = null;
            try{
                fos = new FileOutputStream(clipImageFile);
                resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                if (!resultBitmap.isRecycled()) {
                    resultBitmap.recycle();
                }
                onResult(TakePhotoMain.RESULT_SUCCESS,null, null, clipImageFile.getAbsolutePath());
            }catch (Exception e){
                clipImageFile = null;
                e.printStackTrace();
                LogUtil.d(TAG, "裁剪图片失败：" + e.getMessage());
            } finally {
                try {
                    if (fos != null){
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                closeLoading(loadingDialog);
                finish();
            }
        } else {
            closeLoading(loadingDialog);
            onResult(TakePhotoMain.RESULT_FAIL,"图片文件创建失败", null, null);
            finish();
        }
    }

    /**
     * 结果回调
     * @param Code
     * @param reason
     * @param resultBitmap
     * @param photoPath
     */
    private void onResult(String Code, String reason, Bitmap resultBitmap,String photoPath){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TakePhotoMain.getInstance().onResult(Code,reason, resultBitmap, photoPath);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
        loadingDialog = null;
    }
}

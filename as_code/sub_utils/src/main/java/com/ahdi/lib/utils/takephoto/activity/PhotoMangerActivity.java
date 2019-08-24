package com.ahdi.lib.utils.takephoto.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.ahdi.lib.utils.R;
import com.ahdi.lib.utils.utils.DateUtil;
import com.ahdi.lib.utils.utils.DeviceUtil;
import com.ahdi.lib.utils.base.BaseActivity;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.takephoto.TakePhotoMain;
import com.ahdi.lib.utils.takephoto.util.ImageUtil;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;

import java.io.File;
import java.util.List;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/6/19.
 *
 * 管理获取图片需要的权限  camera 和 sdcard读写权限
 */

public class PhotoMangerActivity extends BaseActivity{
    private static final String TAG = "PhotoMangerActivity";

    /**相机权限的请求码*/
    private static final int REQUEST_CAMERA_PERMISSION = 101;
    /**sd卡写权限的请求码*/
    private static final int REQUEST_WRITE_STORAGE_PERMISSION = 102;
    /**sd卡读权限的请求码*/
    private static final int REQUEST_READ_STORAGE_PERMISSION = 103;

    /** 拍照请求码*/
    private static final int REQUEST_CODE_CAMERA = 110;
    /** 相册请求码*/
    private static final int REQUEST_CODE_GALLERY = 111;
    /**
     * 照片的绝对路径
     */
    private String picturePath = "";
    private int currentType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        if (isMemoryRecover(savedInstanceState)) {
            return;
        }
        currentType = TakePhotoMain.getInstance().currentType;
        if (currentType == TakePhotoMain.TYPE_USER_PHOTO_ALBUM){
            checkWriteStoragePer();
        }else {
            checkCameraPer();
        }
    }

    /**
     * 检查摄像头权限，做相应的处理
     */
    private void checkCameraPer(){

        if (Build.VERSION.SDK_INT < 23 || checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            onCameraGranted();
        }else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
            openAppSettingDetails(getString(R.string.DialogMsg_B0));
        }else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }
    /**
     * 检查sd卡的写入权限，做相应的处理
     */
    private void checkWriteStoragePer(){
        if (Build.VERSION.SDK_INT < 23 || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            onWriteStorageGranted();
        }else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            openAppSettingDetails(getString(R.string.DialogMsg_A0));
        }else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE_PERMISSION);
        }
    }

    /**
     * 检查sd卡的读取权限，做相应的处理
     */
    private void checkReadStoragePer(){
        if (Build.VERSION.SDK_INT < 23 || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            onReadStorageGranted();
        }else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
            openAppSettingDetails(getString(R.string.DialogMsg_A0));
        }else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE_PERMISSION);
        }
    }

    /**
     * 相机权限成功获取
     */
    private void onCameraGranted(){
        if (DeviceUtil.cameraIsCanUse()){
            if (currentType == TakePhotoMain.TYPE_ID_CARD ||
                    currentType == TakePhotoMain.TYPE_PASSPORT_CARD ||
                    currentType == TakePhotoMain.TYPE_KITAS_CARD){
                customCamera();
            }else {
                checkWriteStoragePer();
            }
        }else {
            openAppSettingDetails(getString(R.string.DialogMsg_B0));
        }
    }

    /**
     * 写sdcard权限成功获取
     */
    private void onWriteStorageGranted(){
        checkReadStoragePer();
    }

    /**
     * 读sdcard权限成功获取
     */
    private void onReadStorageGranted(){
        if (currentType == TakePhotoMain.TYPE_USER_PHOTO_ALBUM){
            openAlbum();
        }else {
            openCamera();
        }
    }


    private void customCamera(){
        try{
            Intent intent = new Intent(PhotoMangerActivity.this, TakePhotoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (Exception ex){
            ex.printStackTrace();
            LogUtil.e(TAG, ex.getMessage());
        }finally {
            finish();
        }
    }

    /**
     * 系统相机拍照拍照
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile =  ImageUtil.getImagePath(PhotoMangerActivity.this, DateUtil.formatTimeDMYNoHMS(System.currentTimeMillis()) + ".jpg");
        if (imageFile != null) {
            picturePath = imageFile.getAbsolutePath();
            Uri uri;
            if (Build.VERSION.SDK_INT <= 23) {//6.0
                uri = Uri.fromFile(imageFile);
            } else {
                uri = FileProvider.getUriForFile(this, getPackageName() + ".android.fileprovider", imageFile);
                //加入uri权限 要不三星手机不能拍照
                List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            } else {
                LogUtil.d(TAG, "没有找到相机app");
                onFail("没有找到相机app");
                finish();
            }
        }else{
            LogUtil.d(TAG, "获取图片存储路径失败");
            onFail("获取图片存储路径失败");
            finish();
        }

    }

    private void openAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_GALLERY);
        }else {
            onFail("没有找到相机app");
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK){
            onCancel();
            return;
        }
        if (requestCode == REQUEST_CODE_CAMERA){
            goCropPicture();
        }else if (requestCode == REQUEST_CODE_GALLERY) {
            if (null != data) {
                Uri uri = data.getData();
                if (uri == null){
                    onFail("读取相册图片失败");
                    return;
                }
                String uriStr = uri.toString();
                if (uriStr.startsWith(Constants.START_WITH_FILE)){
                    picturePath = uriStr.replace(Constants.START_WITH_FILE, "");
                }else {
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null){
                        if (cursor.moveToFirst()) {
                            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            picturePath = cursor.getString(columnIndex);
                        }
                        cursor.close();
                    }
                }

                if (TextUtils.isEmpty(picturePath)) {
                    LogUtil.d(TAG, "读取相册图片失败");
                    onFail("读取相册图片失败");
                }else{
                    LogUtil.d(TAG, "读取相册图片path：" + picturePath);
                    goCropPicture();
                }
            } else {
                LogUtil.d(TAG, "从相册获取图片失败");
                onFail("从相册获取图片失败");
            }
        }
    }

    /**
     * 去裁剪图片
     */
    private void goCropPicture(){
        Intent intent = new Intent(PhotoMangerActivity.this, TakePhotoResultActivity.class);
        intent.putExtra(TakePhotoResultActivity.PHOTO_PATH_KEY, picturePath);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        onCancel();
    }

    /**
     * 取消的回调
     */
    private void onCancel(){
        TakePhotoMain.getInstance().onResult(TakePhotoMain.RESULT_CANCEL, "用户取消", null, null);
        finish();
    }

    /**
     * 失败的回调
     * @param errorMsg
     */
    private void onFail(String errorMsg){
        TakePhotoMain.getInstance().onResult(TakePhotoMain.RESULT_FAIL, errorMsg, null, null);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults == null || grantResults.length == 0){
            onFail("权限被拒绝");
            return;
        }
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults[0] != PackageManager.PERMISSION_GRANTED){
            openAppSettingDetails(getString(R.string.DialogMsg_B0));
            return;
        }
        if ((requestCode == REQUEST_WRITE_STORAGE_PERMISSION || requestCode == REQUEST_READ_STORAGE_PERMISSION) && grantResults[0] != PackageManager.PERMISSION_GRANTED){
            openAppSettingDetails(getString(R.string.DialogMsg_A0));
            return;
        }
        if (requestCode == REQUEST_CAMERA_PERMISSION ){
            onCameraGranted();
        }else if (requestCode == REQUEST_WRITE_STORAGE_PERMISSION){
            onWriteStorageGranted();
        }else if (requestCode == REQUEST_READ_STORAGE_PERMISSION){
            onReadStorageGranted();
        }
    }

    protected void openAppSettingDetails(String msg) {
        new CommonDialog
                .Builder(this)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.DialogButton_E0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                        dialog.dismiss();
                        onCancel();
                    }
                }).setNegativeButton(getString(R.string.DialogButton_A0), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onCancel();
            }
        }).show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}

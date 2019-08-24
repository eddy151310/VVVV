package com.ahdi.lib.utils.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

public class PermissionsActivity extends FragmentActivity {

    //  申请类型
    public final int request_type_lbs = 3000; //定位

    public final int request_type_camera = 3001; //相机

    public final int request_type_phoneState = 3002; //手机状态

    public final int request_type_sdCard = 3003; //sd卡读写权限


    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    /**
     * 为子类提供一个权限检查方法
     *
     * @param request_type
     * @return
     */
    public boolean checkPermissions(int request_type) {

        switch (request_type) {
            case request_type_lbs:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                break;
            case request_type_camera:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                break;
            case request_type_phoneState:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                break;
            case request_type_sdCard:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                break;

        }
        return true;
    }

    /**
     * 权限申请方法
     *
     * @param request_type
     */
    public void requestPermissions(int request_type) {
        String[] permissions = null;
        switch (request_type) {
            case request_type_lbs:
                permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
                break;
            case request_type_camera:
                permissions = new String[]{Manifest.permission.CAMERA};
                break;
            case request_type_phoneState:
                permissions = new String[]{Manifest.permission.READ_PHONE_STATE};
                break;
            case request_type_sdCard:
                permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
                break;

        }
        ActivityCompat.requestPermissions(this, permissions, request_type);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LogUtil.d("" , "-----------onRequestPermissionsResult------------");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            doAnything(requestCode);
        } else {
            showDialogTips(this , "请手动打开权限");
        }

    }

    private void showDialogTips(Activity mActivity , String msgTips) {

        builder = new AlertDialog.Builder(mActivity)
                .setMessage("\n" + msgTips + "\n").setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                        dialogInterface.dismiss();

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        doCancle();
                    }
                });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 执行自己的操作
     */
    public void doAnything(int requestCode) {

    }

    /**
     * 执行自己的操作
     */
    public void doCancle() {

    }


    //6.0权限的基本知识，以下是需要单独申请的权限，共分为9组，每组只要有一个权限申请成功了，就默认整组权限都可以使用了。
//
//        group:android.permission-group.CONTACTS
//        permission:android.permission.WRITE_CONTACTS
//        permission:android.permission.GET_ACCOUNTS
//        permission:android.permission.READ_CONTACTS
//
//        group:android.permission-group.PHONE
//        permission:android.permission.READ_CALL_LOG
//        permission:android.permission.READ_PHONE_STATE
//        permission:android.permission.CALL_PHONE
//        permission:android.permission.WRITE_CALL_LOG
//        permission:android.permission.USE_SIP
//        permission:android.permission.PROCESS_OUTGOING_CALLS
//        permission:com.android.voicemail.permission.ADD_VOICEMAIL
//
//        group:android.permission-group.CALENDAR
//        permission:android.permission.READ_CALENDAR
//        permission:android.permission.WRITE_CALENDAR
//
//        group:android.permission-group.CAMERA
//        permission:android.permission.CAMERA
//
//        group:android.permission-group.SENSORS
//        permission:android.permission.BODY_SENSORS
//
//        group:android.permission-group.LOCATION
//        permission:android.permission.ACCESS_FINE_LOCATION
//        permission:android.permission.ACCESS_COARSE_LOCATION
//
//        group:android.permission-group.STORAGE
//        permission:android.permission.READ_EXTERNAL_STORAGE
//        permission:android.permission.WRITE_EXTERNAL_STORAGE
//
//        group:android.permission-group.MICROPHONE
//        permission:android.permission.RECORD_AUDIO
//
//        group:android.permission-group.SMS
//        permission:android.permission.READ_SMS
//        permission:android.permission.RECEIVE_WAP_PUSH
//        permission:android.permission.RECEIVE_MMS
//        permission:android.permission.RECEIVE_SMS
//        permission:android.permission.SEND_SMS
//        permission:android.permission.READ_CELL_BROADCASTS
}
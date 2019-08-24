package com.ahdi.lib.utils.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;

import com.ahdi.lib.utils.utils.DeviceUtil;
import com.ahdi.lib.utils.utils.ToolUtils;

import java.lang.reflect.Method;

/**
 * @author admin
 */
public class PermissionManager {
    /**
     * 相机权限对应的code
     */
    public static final int OP_CAMERA = 26;
    /**
     * 读取手机状态权限对应的code
     */
    public static final int OP_READ_PHONE_STATE = 51;
    /**
     * 读sdcard权限对应的code
     */
    public static final int OP_READ_EXTERNAL_STORAGE = 59;
    /**
     * 写sdcard权限对应的code
     */
    public static final int OP_WRITE_EXTERNAL_STORAGE = 60;
    /**
     * 账户权限对应的code
     */
    public static final int OP_GET_ACCOUNTS = 62;

	/**
	 * 检查权限
	 * @param permission
	 * @return
	 */
	@TargetApi(23) 
	public static boolean checkPermission(Context context,String permission){
		if(context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
			return false;	
		}else{
			return true;
		}
	}
	
	/**
     * 请求权限
     * @return
     */
    @TargetApi(23)
    public static void requestPermission(Activity mActivity,String[]permissions,int requestCode){
        mActivity.requestPermissions(permissions, requestCode);
    }

    /**
     * 反射调用系统权限,
     * 用于判断sdk_int >=23 targetVersion < 23 判断权限是否打开
     *
     * @param permissionCode 相应的权限所对应的code
     * @see {@link AppOpsManager }
     * @return  0标识
     */
    public static int reflectPermission(Context context, int permissionCode) {
        int checkPermission = 0;
        if (Build.VERSION.SDK_INT >= 19) {
            AppOpsManager _manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class<?>[] types = new Class[]{int.class, int.class, String.class};
                Object[] args = new Object[]{permissionCode, Binder.getCallingUid(), context.getPackageName()};
                Method method = _manager.getClass().getDeclaredMethod("noteOp", types);
                method.setAccessible(true);
                Object _o = method.invoke(_manager, args);
                if ((_o instanceof Integer)) {
                    checkPermission = (Integer) _o;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            checkPermission = 0;
        }
        return checkPermission;
    }

    /**
     * 检查权限
     * 适用于各个版本
     * 1、手机系统版本>=23 app目标版本号>=23 使用最新的检测权限方法checkPermission(context,permission);
     * 2、手机系统版本>=23 app目标版本号 < 23 使用反射检测权限方法 reflectPermission(context, permissionCode)
     * 3、手机系统版本< 23 默认有权限
     * @param context
     * @param permission 例如 Manifest.permission.READ_PHONE_STATE
     * @param permissionCode 例如 PermissionManager.OP_READ_PHONE_STATE
     * @return
     */
    public static boolean checkPermission(Context context, String permission, int permissionCode){

        if (Build.VERSION.SDK_INT >= 23){
            if(DeviceUtil.getTargetVersion(context) >= 23){
                return checkPermission(context,permission);
            }else{
               return reflectPermission(context, permissionCode) == 0;
            }
        }else{
            return context.getPackageManager().checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
        }
    }
}

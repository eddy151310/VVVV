package com.ahdi.lib.utils.widgets;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.R;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;

/**
 * Created by Administrator on 2018/4/26.
 */

public class CheckSafety {

    public static boolean checkSafe(Activity activity, Class<?> intentClass) {
        String statue = ProfileUserUtil.getInstance().getAccountStatus();
        if (!TextUtils.equals(statue, Constants.STATUE_UNSAFE_KEY)) {
            return true;
        } else {
            showDialog(activity, intentClass);
            return false;
        }
    }

    private static void showDialog(Activity mActivity, Class<?> intentClass) {
        new CommonDialog
                .Builder(mActivity)
                .setTitle(mActivity.getString(R.string.DialogTitle_B0))
                .setMessage(mActivity.getString(R.string.DialogMsg_C0))
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(mActivity.getString(R.string.DialogButton_B0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openGuideSet(mActivity, intentClass);
                        dialog.dismiss();
                    }
                }).show();
    }

    private static void openGuideSet(Activity mActivity, Class<?> intentClass) {
        Intent intent = new Intent(mActivity, intentClass);
        intent.putExtra(Constants.LOCAL_FROM_KEY, Constants.LOCAL_PAYMENT_FROM_GUIDE_SET);
        mActivity.startActivity(intent);
    }

    public static boolean checkSafeTouchID(Activity activity, Class<?> intentClass) {
        String statue = ProfileUserUtil.getInstance().getAccountStatus();
        if (!TextUtils.equals(statue, Constants.STATUE_UNSAFE_KEY)) {
            return true;
        } else {
            openGuideSet(activity, intentClass);
            return false;
        }
    }
}

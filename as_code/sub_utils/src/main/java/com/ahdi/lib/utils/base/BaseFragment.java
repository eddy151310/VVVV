package com.ahdi.lib.utils.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.ahdi.lib.utils.R;
import com.ahdi.lib.utils.utils.ToolUtils;
import com.ahdi.lib.utils.utils.UMengUtil;
import com.ahdi.lib.utils.widgets.dialog.CommonDialog;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;

/**
 * Date: 2017/12/27 上午11:42
 * Author: kay lau
 * Description:
 */
public abstract class BaseFragment extends Fragment {

    protected Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = initRootView(inflater, container);
        initView(view);
        initData(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    protected abstract View initRootView(LayoutInflater inflater, ViewGroup container);

    protected abstract void initView(View view);

    protected abstract void initData(View view);

    protected void openAppSettingDetails() {
        new CommonDialog
                .Builder(mActivity)
                .setMessage(getString(com.ahdi.lib.utils.R.string.DialogMsg_B0))
                .setCancelable(false)
                .setPositiveButton(mActivity.getString(com.ahdi.lib.utils.R.string.DialogButton_E0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                }).setNegativeButton(mActivity.getString(com.ahdi.lib.utils.R.string.DialogButton_A0), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden()){
            UMengUtil.onPageStart(getClass().getSimpleName().intern());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!isHidden()){
            UMengUtil.onPageEnd(getClass().getSimpleName().intern());
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            UMengUtil.onPageEnd(getClass().getSimpleName().intern());
        }
    }

    protected void closeLoading(LoadingDialog dialog) {
        LoadingDialog.dismissDialog(dialog);
    }

    protected LoadingDialog showLoading() {
        return LoadingDialog.showDialogLoading(mActivity, getString(R.string.DialogTitle_C0));
    }

    /**
     * @return true 可再次点击, false 500毫秒之内不可点击
     */
    protected boolean isCanClick() {
        return ToolUtils.isCanClick();
    }

    private static final long offSetDifference = 3 * 1000;
    public long tLastClickTime = 0;
    /**
     * 防止多次请求
     */
    public boolean isQuest() {
        try {
            long time = System.currentTimeMillis();
            long offSetTime = time - tLastClickTime;
            if (Math.abs(offSetTime) > offSetDifference) {
                tLastClickTime = time;
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public void closeSoftInput() {
        if (mActivity == null || mActivity.isFinishing()){
            return;
        }
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mActivity.getWindow().getDecorView().getWindowToken(), 0);
    }

    public void showErrorDialog(String errorMsg){
        if (mActivity == null || mActivity.isFinishing()){
            return;
        }
        new CommonDialog
                .Builder(getActivity())
                .setMessage(errorMsg)
                .setMessageCenter(true)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.DialogButton_B0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

}

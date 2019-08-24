package com.ahdi.wallet.app.callback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * @author admin
 * 通过activity生命周期的回调来判断app处于前台还是后台
 */

public class ActivityLifeCallBack implements Application.ActivityLifecycleCallbacks {
    private static int started;
    private static int stopped;
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        started++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        stopped++;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    /**
     * app是否在前台
     *
     * @return true 前台 false 后台
     */
    public boolean isForeground(){
        return started > stopped;
    }
}

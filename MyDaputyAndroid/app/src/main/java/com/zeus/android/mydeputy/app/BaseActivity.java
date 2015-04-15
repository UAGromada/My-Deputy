package com.zeus.android.mydeputy.app;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.local.LocalManager;
import com.zeus.android.mydeputy.app.local.PreferencesManager;

/**
 * Created by admin on 2/6/15.
 */
public class BaseActivity extends ActionBarActivity {

    public  static final String TAG = BaseActivity.class.getSimpleName();

    private RequestManager requestManager;
    private PreferencesManager preferencesManager;
    private LocalManager localManager;

    public RequestManager getRequestManager() {
        if(requestManager == null) requestManager = App.getInstance().getRequestManager();
        return requestManager;
    }

    public PreferencesManager getPreferencesManager() {
        if(preferencesManager== null) preferencesManager = App.getInstance().getPreferencesManager();
        return preferencesManager;
    }

    public LocalManager getLocalManager() {
        if(localManager== null) localManager = App.getInstance().getLocalManager();
        return localManager;
    }


    public void setStatusBarColor(View statusBar,int color){

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(color));
            getWindow().setStatusBarColor(getResources().getColor(color));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int statusBarHeight = getStatusBarHeight();
            //action bar height
            statusBar.getLayoutParams().height = statusBarHeight;
            statusBar.setBackgroundColor(color);
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}

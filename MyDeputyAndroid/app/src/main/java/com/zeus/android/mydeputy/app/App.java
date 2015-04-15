package com.zeus.android.mydeputy.app;

import android.app.Application;

import com.zeus.android.mydeputy.app.local.LocalManager;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.local.PreferencesManager;

/**
 * Created by admin on 2/2/15.
 */
public class App extends Application {

    private static final String TAG = App.class.getSimpleName();
    public static final String SP_FILE_NAME = "prefs_f";

    private static App app;

    private LocalManager mLocalManager;
    private RequestManager mRequestManager;
    private PreferencesManager preferencesManager;

    public static App getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        mLocalManager = new LocalManager();
    }

    public LocalManager getLocalManager() {
        if (mLocalManager == null) mLocalManager = new LocalManager();
        return mLocalManager;
    }

    public RequestManager getRequestManager(){
        if (mRequestManager == null) mRequestManager = new RequestManager(app.getApplicationContext());
        return mRequestManager;
    }

    public PreferencesManager getPreferencesManager() {
        if (preferencesManager == null) preferencesManager = new PreferencesManager();
        return preferencesManager;
    }
}

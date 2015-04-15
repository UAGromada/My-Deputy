package com.zeus.android.mydeputy.app;

import android.support.v4.app.Fragment;

import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.local.LocalManager;
import com.zeus.android.mydeputy.app.local.PreferencesManager;

/**
 * Created by admin on 2/23/15.
 */
public class BaseFragment extends Fragment {

    public final String TAG = BaseFragment.class.getSimpleName();

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
}

package com.zeus.android.mydeputy.app.deputy;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.zeus.android.mydeputy.app.BaseActivity;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.model.Citizen;
import com.zeus.android.mydeputy.app.api.request.CitizenInfoRequest;
import com.zeus.android.mydeputy.app.api.response.CitizenInfoResponse;
import com.zeus.android.mydeputy.app.deputy.fragments.AppealCitizenAppeal;
import com.zeus.android.mydeputy.app.deputy.fragments.AppealCitizenInfo;
import com.zeus.android.mydeputy.app.deputy.fragments.AppealCitizenQuiz;
import com.zeus.android.mydeputy.app.ui.ErrorLayout;

import org.json.JSONObject;

/**
 * Created by admin on 1/28/15.
 */
public class AppealCitizenActivity extends BaseActivity implements
        RequestManager.OnResponseListener,
        View.OnClickListener{

    public static final String TAG = AppealCitizenActivity.class.getSimpleName();
    public static final String CITIZEN_ID = "citizen_id";

    private Toolbar toolbar;
    private ViewPager viewPager;
    private ErrorLayout errorLayout;

    private FragmentManager fragmentManager;

    private int citizenId;
    private Citizen citizen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deputy_main_appeal_citizen);

        errorLayout = (ErrorLayout) findViewById(R.id.error_layout);
        errorLayout.setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager = getSupportFragmentManager();

        citizenId= getIntent().getExtras().getInt(CITIZEN_ID);

        sendCitizenInfoRequest();

        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.main_dark));
    }

    @Override
    protected void onStart() {
        super.onStart();
        getRequestManager().addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getRequestManager().removeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type){
            case RequestManager.REQUEST_CITIZEN_INFO:
                CitizenInfoResponse citizenInfoResponse = new Gson().fromJson(response.toString(), CitizenInfoResponse.class);
                citizen = citizenInfoResponse.getCitizen();
                viewPager = (ViewPager) findViewById(R.id.deputy_appeal_citizen_info_pager);
                viewPager.setAdapter(new FragmentsAdapter(fragmentManager, getApplicationContext()));

                errorLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {
        errorLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.error_retry:
                sendCitizenInfoRequest();
                break;
        }
    }

    public class FragmentsAdapter extends FragmentPagerAdapter{

        public FragmentsAdapter(FragmentManager fm, Context context) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            if (i == 0) {
                fragment = new AppealCitizenInfo();
            }
            if (i == 1) {
                fragment = new AppealCitizenAppeal();
            }
            if (i == 2) {
                fragment = new AppealCitizenQuiz();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String fragmentTitle = new String();
            if (position == 0) {
                fragmentTitle = getApplicationContext().getResources().getString(R.string.txt_main_info);
            }
            if (position == 1) {
                fragmentTitle = getApplicationContext().getResources().getString(R.string.txt_appeal);
            }
            if (position == 2) {
                fragmentTitle = getApplicationContext().getResources().getString(R.string.txt_quiz);
            }

            return fragmentTitle;
        }
    }

    public Citizen getCitizen(){
        return  citizen;
    }

    private void sendCitizenInfoRequest(){
        CitizenInfoRequest request = new CitizenInfoRequest();
        request.setEmail(getPreferencesManager().getEmail());
        request.setHash(getPreferencesManager().getPasswordHash());
        request.setCitizen_id(citizenId);

        getRequestManager().sendRequest(request, RequestManager.CITIZEN_INFO_URL, TAG, RequestManager.REQUEST_CITIZEN_INFO);
    }
}

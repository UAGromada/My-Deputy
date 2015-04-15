package com.zeus.android.mydeputy.app.citizen.main_fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.zeus.android.mydeputy.app.App;
import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.model.Deputy;
import com.zeus.android.mydeputy.app.api.request.DeputyInfoRequest;
import com.zeus.android.mydeputy.app.api.response.DeputyInfoResponse;
import com.zeus.android.mydeputy.app.citizen.CitizenMainActivity;
import com.zeus.android.mydeputy.app.citizen.fragments.InfoContactsFragment;
import com.zeus.android.mydeputy.app.citizen.fragments.InfoProgramFragment;
import com.zeus.android.mydeputy.app.citizen.fragments.InfoPromisesFragment;
import com.zeus.android.mydeputy.app.ui.ErrorLayout;

import org.json.JSONObject;

/**
 * Created by admin on 2/13/15.
 */
public class InfoFragment extends BaseFragment implements
        RequestManager.OnResponseListener,
        View.OnClickListener{

    public static final String TAG = InfoFragment.class.getSimpleName();

    private ViewPager mViewPager;
    private ErrorLayout errorLayout;

    private FragmentManager fragmentManager;
    private Deputy deputy;

    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.citizen_main_info, container, false);

        deputy = App.getInstance().getLocalManager().getCurrentDeputy();

        errorLayout = (ErrorLayout) v.findViewById(R.id.error_layout);
        errorLayout.setOnClickListener(this);

        fragmentManager = getActivity().getSupportFragmentManager();

        mViewPager = (ViewPager) v.findViewById(R.id.citizen_deputy_info_pager);

        activity.setTitle(activity.getResources().getString(R.string.title_deputy_info));
        sendDeputyInfoRequest();

        return  v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getRequestManager().addListener(this);
        ((CitizenMainActivity)activity).toggleShadow(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        getRequestManager().removeListener(this);
        ((CitizenMainActivity)activity).toggleShadow(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    private void sendDeputyInfoRequest(){
        DeputyInfoRequest request = new DeputyInfoRequest();
        request.setEmail(getPreferencesManager().getEmail());
        request.setHash(getPreferencesManager().getPasswordHash());
        request.setDeputyId(getPreferencesManager().getDeputyId());

        getRequestManager().sendRequest(request, RequestManager.DEPUTY_INFO_URL, TAG, RequestManager.REQUEST_DEPUTY_INFO);
    }

    @Override
    public void onClick(View v) {
        sendDeputyInfoRequest();
    }

    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type){
            case RequestManager.REQUEST_DEPUTY_INFO:
                DeputyInfoResponse deputyInfoResponse = new Gson().fromJson(response.toString(), DeputyInfoResponse.class);

                getLocalManager().getCurrentDeputy().setPromises(deputyInfoResponse.getDeputy().getPromises());
                getLocalManager().getCurrentDeputy().setProgram(deputyInfoResponse.getDeputy().getProgram());
                getLocalManager().getCurrentDeputy().setContacts(deputyInfoResponse.getDeputy().getContacts());

                Deputy.Contacts contacts = getLocalManager().getCurrentDeputy().getContacts();

                mViewPager.setAdapter(new FragmentsAdapter(fragmentManager, activity));
                errorLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {
        switch (type){
            case RequestManager.REQUEST_DEPUTY_INFO:
                errorLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    public class FragmentsAdapter extends FragmentStatePagerAdapter {

        public FragmentsAdapter(FragmentManager fm, Context context) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            if (i == 0) {
                fragment = new InfoProgramFragment();
            }
            if (i == 1) {
                fragment = new InfoPromisesFragment();
            }
            if (i == 2) {
                fragment = new InfoContactsFragment();
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
                fragmentTitle = activity.getResources().getString(R.string.deputy_info_program_tab);
            }
            if (position == 1) {
                fragmentTitle = activity.getResources().getString(R.string.deputy_info_promises_tab);
            }
            if (position == 2) {
                fragmentTitle = activity.getResources().getString(R.string.deputy_info_contacts_tab);
            }

            return fragmentTitle;
        }
    }
}

package com.zeus.android.mydeputy.app.deputy.main_fragments;

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

import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.deputy.DeputyMainActivity;
import com.zeus.android.mydeputy.app.deputy.fragments.AppealAcceptedFragment;
import com.zeus.android.mydeputy.app.deputy.fragments.AppealNewFragment;

/**
 * Created by admin on 2/16/15.
 */
public class AppealFragment extends BaseFragment{

    public static final String TAG = AppealFragment.class.getSimpleName();

    private ViewPager viewPager;

    private Activity activity;

    private FragmentManager fragmentManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.deputy_main_appeal, container, false);

        fragmentManager = getActivity().getSupportFragmentManager();

        viewPager = (ViewPager) v.findViewById(R.id.deputy_appeal_pager);
        viewPager.setAdapter(new FragmentsAdapter(fragmentManager, activity));

        activity.setTitle(activity.getResources().getString(R.string.title_deputy_appeals));

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DeputyMainActivity)activity).toggleShadow(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((DeputyMainActivity)activity).toggleShadow(true);
    }

    public class FragmentsAdapter extends FragmentStatePagerAdapter {

        public FragmentsAdapter(FragmentManager fm,  Context context){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            if(i==0){
                fragment = new AppealNewFragment();
            }
            if(i == 1){
                fragment = new AppealAcceptedFragment();
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String fragmentTitle = new String();
            if(position == 0){
                fragmentTitle = activity.getResources().getString(R.string.txt_appeal_new);
            }
            if(position == 1){
                fragmentTitle = activity.getResources().getString(R.string.txt_appeal_accepted);
            }

            return fragmentTitle;
        }
    }
}

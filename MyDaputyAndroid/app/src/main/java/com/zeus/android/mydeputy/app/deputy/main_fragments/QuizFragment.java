package com.zeus.android.mydeputy.app.deputy.main_fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;
import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.deputy.DeputyMainActivity;
import com.zeus.android.mydeputy.app.deputy.QuizCreateActivity;
import com.zeus.android.mydeputy.app.deputy.fragments.QuizFinishedFragment;
import com.zeus.android.mydeputy.app.deputy.fragments.QuizInProcessFragment;

/**
 * Created by admin on 2/16/15.
 */
public class QuizFragment extends BaseFragment implements View.OnClickListener{

    public static final String TAG = QuizFragment.class.getSimpleName();

    private ViewPager viewPager;
    protected FloatingActionButton createQuiz;

    private Activity activity;

    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.deputy_main_quiz, container, false);

        createQuiz = (FloatingActionButton) v.findViewById(R.id.deputy_quiz_btn_create_new);
        createQuiz.setOnClickListener(this);

        fragmentManager = getActivity().getSupportFragmentManager();

        viewPager = (ViewPager) v.findViewById(R.id.deputy_quiz_pager);
        viewPager.setAdapter(new FragmentsAdapter(fragmentManager, activity));

        activity.setTitle(activity.getResources().getString(R.string.title_deputy_quiz));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.deputy_quiz_btn_create_new:
                Intent i = new Intent(activity, QuizCreateActivity.class);
                startActivity(i);
                break;
        }
    }


    public void setFloatButtonList(ListView lv){
        createQuiz.attachToListView(lv);
    }

    public class FragmentsAdapter extends FragmentStatePagerAdapter {

        public FragmentsAdapter(FragmentManager fm,  Context context){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            if(i==0){
                fragment = new QuizInProcessFragment();
            }
            if(i == 1){
                fragment = new QuizFinishedFragment();;
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
                fragmentTitle = activity.getResources().getString(R.string.txt_quiz_in_process);
            }
            if(position == 1){
                fragmentTitle = activity.getResources().getString(R.string.txt_quiz_finished);;
            }

            return fragmentTitle;
        }
    }

}

package com.zeus.android.mydeputy.app.deputy;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.zeus.android.mydeputy.app.App;
import com.zeus.android.mydeputy.app.BaseActivity;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.local.PreferencesManager;
import com.zeus.android.mydeputy.app.local.LocalManager;
import com.zeus.android.mydeputy.app.deputy.main_fragments.AppealFragment;
import com.zeus.android.mydeputy.app.deputy.main_fragments.InfoFragment;
import com.zeus.android.mydeputy.app.deputy.main_fragments.NavigationDrawerFragment;
import com.zeus.android.mydeputy.app.deputy.main_fragments.NewsFragment;
import com.zeus.android.mydeputy.app.deputy.main_fragments.QuizFragment;

public class DeputyMainActivity extends BaseActivity {

    public static final String TAG = DeputyMainActivity.class.getSimpleName();
    public static final String CURR_FRAGMENT_ID = "curr_fragment_id";
    public static final String NAVIGATION = "navigation";

    public final static int FRAGMENT_NEWS = 0;
    public final static int FRAGMENT_APPEAL = 1;
    public final static int FRAGMENT_QUIZ = 2;
    public final static int FRAGMENT_INFO = 3;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private View shadow;

    private FragmentManager fragmentManager;

    private int currFragmentId;
    private Fragment currFragment;

    private NavigationDrawerFragment drawerFragment;
    private boolean navigationDrawerOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.citizen_main);

        fragmentManager = getSupportFragmentManager();

        shadow = (View) findViewById(R.id.shadow);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        setDrawerLayout();

        if(savedInstanceState != null){
            currFragmentId = savedInstanceState.getInt(CURR_FRAGMENT_ID);
            if (savedInstanceState.getBoolean(NAVIGATION)) {
                drawerFragment = (NavigationDrawerFragment)fragmentManager.findFragmentByTag(NavigationDrawerFragment.TAG);
            }
        }else {
            openFragment(FRAGMENT_NEWS);
        }

        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.main_dark));
    }

    @Override
    protected void onStart() {
        super.onStart();
        drawerFragment.setUp(drawerLayout, toolbar);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(CURR_FRAGMENT_ID, currFragmentId);
        outState.putBoolean(NAVIGATION, drawerFragment.isOpen());
    }

    private void setDrawerLayout(){
        drawerFragment = new NavigationDrawerFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_navigation_drawer_container, drawerFragment, NavigationDrawerFragment.TAG);
        fragmentTransaction.commit();
    }

    public void openFragment(int id){

        String tag = null;
        if (currFragment != null){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(currFragment);
            fragmentTransaction.commit();
        }

        switch (id){
            case FRAGMENT_INFO:
                currFragment = new InfoFragment();
                tag = InfoFragment.TAG;
                getLocalManager().setCurrentOpenFragmentId(FRAGMENT_INFO);
                break;
            case FRAGMENT_NEWS:
                currFragment = new NewsFragment();
                tag = InfoFragment.TAG;
                getLocalManager().setCurrentOpenFragmentId(FRAGMENT_NEWS);
                break;
            case FRAGMENT_APPEAL:
                currFragment = new AppealFragment();
                tag = InfoFragment.TAG;
                getLocalManager().setCurrentOpenFragmentId(FRAGMENT_APPEAL);
                break;
            case FRAGMENT_QUIZ:
                currFragment = new QuizFragment();
                tag = InfoFragment.TAG;
                getLocalManager().setCurrentOpenFragmentId(FRAGMENT_QUIZ);
                break;
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.citizen_main_fragments_container, currFragment, tag);
        fragmentTransaction.commit();
    }

    public void closeNavigationDrawer(){
        drawerLayout.closeDrawers();
    }

    public void toggleShadow(boolean state){
        if (state){
            shadow.setVisibility(View.VISIBLE);
        }else{
            shadow.setVisibility(View.INVISIBLE);
        }
    }
}

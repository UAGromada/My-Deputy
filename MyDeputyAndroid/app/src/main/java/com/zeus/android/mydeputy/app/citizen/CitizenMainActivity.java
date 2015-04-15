package com.zeus.android.mydeputy.app.citizen;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.zeus.android.mydeputy.app.App;
import com.zeus.android.mydeputy.app.BaseActivity;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.local.LocalManager;
import com.zeus.android.mydeputy.app.local.PreferencesManager;
import com.zeus.android.mydeputy.app.citizen.main_fragments.NavigationDrawerFragment;
import com.zeus.android.mydeputy.app.citizen.main_fragments.AppealFragment;
import com.zeus.android.mydeputy.app.citizen.main_fragments.InfoFragment;
import com.zeus.android.mydeputy.app.citizen.main_fragments.ListFragment;
import com.zeus.android.mydeputy.app.citizen.main_fragments.NewsFragment;
import com.zeus.android.mydeputy.app.citizen.main_fragments.QuizFragment;

/**
 * Created by admin on 2/13/15.
 */
public class CitizenMainActivity extends BaseActivity {

    public static final String TAG = CitizenMainActivity.class.getSimpleName();
    public static final String CURR_FRAGMENT_ID = "curr_fragment_id";
    public static final String CURR_FRAGMENT_TAG = "curr_fragment_tag";
    public static final String NAVIGATION = "navigation";

    public final static int FRAGMENT_INFO = 0;
    public final static int FRAGMENT_NEWS = 1;
    public final static int FRAGMENT_APPEAL = 2;
    public final static int FRAGMENT_QUIZ = 3;
    public final static int FRAGMENT_LIST = 4;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private View shadow;

    private FragmentManager fragmentManager;
    private Fragment currFragment;
    private NavigationDrawerFragment drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.citizen_main);

        fragmentManager = getSupportFragmentManager();

        shadow = (View) findViewById(R.id.shadow);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        drawerFragment = (NavigationDrawerFragment)fragmentManager.findFragmentByTag(NavigationDrawerFragment.TAG);
        if (drawerFragment == null) setDrawerLayout();

        if (getPreferencesManager().getDeputyId() == 0) {
            openFragment(FRAGMENT_LIST);
        } else {
            openFragment(FRAGMENT_INFO);
        }

        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.main_dark));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getPreferencesManager().getDeputyId() != 0) {
            enableDrawerNavigation();
        }
    }

    public void enableDrawerNavigation(){
        drawerFragment.setUp(drawerLayout, toolbar);
    }

    public void refreshDrawerNavigation(){
        drawerFragment.refreshDeputy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

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
            case FRAGMENT_LIST:
                currFragment = new ListFragment();
                tag = ListFragment.TAG;
                getLocalManager().setCurrentOpenFragmentId(FRAGMENT_LIST);
                break;
        }

        drawerFragment.notifyNavigationDataChanges();

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

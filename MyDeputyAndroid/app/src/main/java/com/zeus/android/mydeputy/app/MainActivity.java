package com.zeus.android.mydeputy.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.zeus.android.mydeputy.app.citizen.CitizenMainActivity;
import com.zeus.android.mydeputy.app.deputy.DeputyMainActivity;
import com.zeus.android.mydeputy.app.fragment.LoginFragment;
import com.zeus.android.mydeputy.app.fragment.RegisterFragment;
import com.zeus.android.mydeputy.app.local.PreferencesManager;

public class MainActivity extends BaseActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    public final static int CITIZEN = 1;
    public final static int DEPUTY = 2;

    public final static int LOGIN_TAG = 1;
    public final static int REGISTER_TAG = 2;

    public final static String CUR_FRAG = "cut_frag";

    private static final int NO_ANIMATION = 0;

    private Toolbar mToolbar;
    private FragmentManager fragmentManager;
    private int curFragment;
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.depth_page_in, NO_ANIMATION);
        setContentView(R.layout.main);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(4);

        fragmentManager = getSupportFragmentManager();
        loginFragment = new LoginFragment();


        if (new PreferencesManager().getSaveLogin()) {

            int role = App.getInstance().getPreferencesManager().getRole();
            skipLogin(role);

        }else {
            if (savedInstanceState != null) {
                if (curFragment == REGISTER_TAG) {
                    loginFragment = (LoginFragment)fragmentManager.findFragmentByTag(LoginFragment.TAG);
                } else {
                    registerFragment = (RegisterFragment)fragmentManager.findFragmentByTag(RegisterFragment.TAG);
                }
            } else {
                openLoginFragment();
            }
        }


        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.main_dark));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CUR_FRAG, curFragment);
    }

    public void openRegisterFragment(){
        registerFragment = new RegisterFragment();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frag_container, registerFragment, RegisterFragment.TAG).commit();

        curFragment = REGISTER_TAG;
    }

    public void openLoginFragment(){
        if (loginFragment == null){
            loginFragment = new LoginFragment();
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(fragmentManager.findFragmentByTag(RegisterFragment.TAG) != null){
            fragmentTransaction.replace(R.id.frag_container, loginFragment, LoginFragment.TAG).commit();
        }else {
            fragmentTransaction.add(R.id.frag_container, loginFragment, LoginFragment.TAG).commit();
        }
        curFragment = LOGIN_TAG;
    }

    @Override
    public void onBackPressed() {
        switch(curFragment){
            case LOGIN_TAG:
                finish();
                break;
            case REGISTER_TAG:
                openLoginFragment();
                break;
        }
    }

    private void skipLogin(int role){
        Class c;
        if (role == CITIZEN) {
            c = CitizenMainActivity.class;
        } else {
            c = DeputyMainActivity.class;
        }

        Intent i = new Intent(this, c);
        startActivity(i);
        finish();
    }
}

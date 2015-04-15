package com.zeus.android.mydeputy.app.citizen;

import com.zeus.android.mydeputy.app.BaseActivity;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.api.request.NewsSingleRequest;
import com.zeus.android.mydeputy.app.citizen.fragments.NewsSingleCommentFragment;
import com.zeus.android.mydeputy.app.citizen.fragments.NewsSingleFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class NewsSingleActivity extends BaseActivity {

    public static final String TAG = NewsSingleActivity.class.getSimpleName();
    public static final String NEWS_KEY = "SelectedNewsItemActivityNewsKey";

    private int newsId;

    private Toolbar toolbar;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private NewsSingleCommentFragment newsSingleCommentFragment;
    private NewsSingleFragment newsSingleFragment;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        newsId = getIntent().getExtras().getInt(NEWS_KEY);
        setContentView(R.layout.citizen_main_news_single_main);

        fragmentManager = getSupportFragmentManager();

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (arg0 == null) {
            openNewsFragment();
        }

        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.main_dark));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.findFragmentByTag(NewsSingleCommentFragment.TAG) != null){
            openNewsFragment();
        }else {
            finish();
        }
    }

    public void sendNewsSingleRequest(){

        NewsSingleRequest request = new NewsSingleRequest();
        request.setHash(getPreferencesManager().getPasswordHash());
        request.setEmail(getPreferencesManager().getEmail());
        request.setNewsId(newsId);

        getRequestManager().sendRequest(request, RequestManager.NEWS_SINGLE_URL, TAG, RequestManager.REQUEST_NEWS_SINGLE);
    }

    public void openNewsFragment(){
        sendNewsSingleRequest();
        if (newsSingleFragment == null) newsSingleFragment = NewsSingleFragment.newInstance(newsId);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.citizen_single_news_container, newsSingleFragment, NewsSingleFragment.TAG);
        fragmentTransaction.commit();
    }

    public void openCommentsFragment(){
        sendNewsSingleRequest();
        if (newsSingleCommentFragment == null) newsSingleCommentFragment = NewsSingleCommentFragment.newInstance(newsId);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.citizen_single_news_container, newsSingleCommentFragment, NewsSingleCommentFragment.TAG);
        fragmentTransaction.commit();
    }
}

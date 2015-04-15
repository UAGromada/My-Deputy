package com.zeus.android.mydeputy.app.deputy;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.zeus.android.mydeputy.app.BaseActivity;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.api.request.NewsCreateRequest;
import com.zeus.android.mydeputy.app.fragment.LoadDialogFragment;
import com.zeus.android.mydeputy.app.ui.ScrollViewWithVerticalScrollables;

import org.json.JSONObject;

/**
 * Created by admin on 2/17/15.
 */
public class NewsCreateActivity extends BaseActivity implements RequestManager.OnResponseListener{

    public static final String TAG = NewsCreateActivity.class.getSimpleName();
    public static final String LOAD = "load";

    private Toolbar toolbar;
    private ScrollViewWithVerticalScrollables mTextContainer;
    private EditText title;
    private EditText text;

    private LoadDialogFragment loadDialogFragment;
    private boolean loadDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deputy_main_news_create);

        title = (EditText) findViewById(R.id.deputy_news_new_title);
        text = (EditText) findViewById(R.id.deputy_news_new_text);
        mTextContainer = (ScrollViewWithVerticalScrollables) findViewById(R.id.deputy_appeals_add_scrollcontainer);

        title.setPadding(getDipsFromPixel(16), getDipsFromPixel(8), getDipsFromPixel(16), getDipsFromPixel(8));
        text.setPadding(getDipsFromPixel(16), getDipsFromPixel(8), getDipsFromPixel(16), getDipsFromPixel(8));

        mTextContainer.addScrollableId(R.id.deputy_appeals_add_header);
        mTextContainer.addScrollableId(R.id.deputy_appeals_add_text);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null){
            loadDialog = savedInstanceState.getBoolean(LOAD);
            if (loadDialog){
                showLoadDialog();
            }
        }

        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.main_dark));
    }

    @Override
    public void onStart() {
        super.onStart();
        getRequestManager().addListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getRequestManager().removeListener(this);
    }

    public int getDipsFromPixel(float pixels) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(LOAD, loadDialog);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (loadDialogFragment != null ){
            loadDialogFragment.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.tool_bar_done:
                sendNewsCreateRequest();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type) {
            case RequestManager.REQUEST_NEWS_CREATE:
                dismissLoadDialog();
                this.onBackPressed();
                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {
        switch (type) {
            case RequestManager.REQUEST_NEWS_CREATE:
                dismissLoadDialog();
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void sendNewsCreateRequest(){

        String title = this.title.getText().toString();
        String text = this.text.getText().toString();

        if(title.isEmpty() || text.isEmpty()){
            Toast.makeText(this, getResources().getString(R.string.txt_error_all), Toast.LENGTH_SHORT).show();
        }else {
            showLoadDialog();

            NewsCreateRequest request = new NewsCreateRequest();
            request.setHash(getPreferencesManager().getPasswordHash());
            request.setEmail(getPreferencesManager().getEmail());
            request.setTitle(title.trim());
            request.setText(text.trim());

            getRequestManager().sendRequest(request, RequestManager.NEWS_CREATE_URL, TAG, RequestManager.REQUEST_NEWS_CREATE);
        }
    }

    private void showLoadDialog(){
        loadDialog = true;
        if (loadDialogFragment == null) {
            loadDialogFragment = LoadDialogFragment.newInstance(getResources().getString(R.string.txt_load_create_news));
            loadDialogFragment.setRetainInstance(true);
            loadDialogFragment.setCancelable(false);
            loadDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Base_Theme_AppCompat_Light_Dialog_FixedSize);
        }
        loadDialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    private void dismissLoadDialog() {
        if (loadDialogFragment != null) {
            loadDialogFragment.dismiss();
            loadDialog = false;
        }
    }
}

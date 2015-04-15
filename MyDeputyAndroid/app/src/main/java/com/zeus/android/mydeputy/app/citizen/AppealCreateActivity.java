package com.zeus.android.mydeputy.app.citizen;

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
import com.zeus.android.mydeputy.app.api.request.AppealCreateRequest;
import com.zeus.android.mydeputy.app.fragment.LoadDialogFragment;
import com.zeus.android.mydeputy.app.ui.ScrollViewWithVerticalScrollables;

import org.json.JSONObject;

/**
 * Created by admin on 2/16/15.
 */
public class AppealCreateActivity extends BaseActivity implements RequestManager.OnResponseListener {

    public static final String TAG = AppealCreateActivity.class.getSimpleName();
    public static final String LOAD = "load";

    private ScrollViewWithVerticalScrollables mTextContainer;
    private EditText mHeader;
    private EditText mText;
    private Toolbar toolbar;

    private LoadDialogFragment loadDialogFragment;
    private boolean loadDialog = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.citizen_main_appeal_create);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTextContainer = (ScrollViewWithVerticalScrollables) findViewById(R.id.deputy_appeals_add_scrollcontainer);
        mHeader = (EditText) findViewById(R.id.deputy_appeals_add_header);
        mText = (EditText) findViewById(R.id.deputy_appeals_add_text);

        mHeader.setPadding(getDipsFromPixel(16), getDipsFromPixel(8), getDipsFromPixel(16), getDipsFromPixel(8));
        mText.setPadding(getDipsFromPixel(16), getDipsFromPixel(8), getDipsFromPixel(16), getDipsFromPixel(8));

        mTextContainer.addScrollableId(R.id.deputy_appeals_add_header);
        mTextContainer.addScrollableId(R.id.deputy_appeals_add_text);

        if (savedInstanceState != null) {
            loadDialog = savedInstanceState.getBoolean(LOAD);
            if (loadDialog) {
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
        if (loadDialogFragment != null) {
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
                sendAppealCreateRequest();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendAppealCreateRequest() {

        String header = mHeader.getText().toString();
        String text = mText.getText().toString();

        if (header.isEmpty() || text.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.txt_error_all), Toast.LENGTH_SHORT).show();
        } else {
            showLoadDialog();

            AppealCreateRequest request = new AppealCreateRequest();
            request.setHash(getPreferencesManager().getPasswordHash());
            request.setEmail(getPreferencesManager().getEmail());
            request.setTitle(header.trim());
            request.setMessage(text.trim());
            request.setDeputyId(getLocalManager().getCurrentDeputy().getId());

            getRequestManager().sendRequest(request, RequestManager.APPEAL_CREATE_URL, TAG, RequestManager.REQUEST_APPEAL_CREATE);
        }
    }

    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type) {
            case RequestManager.REQUEST_APPEAL_CREATE:
                dismissLoadDialog();
                finish();
                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {
        switch (type) {
            case RequestManager.REQUEST_APPEAL_CREATE:
                dismissLoadDialog();
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showLoadDialog() {
        loadDialog = true;
        if (loadDialogFragment == null) {
            loadDialogFragment = LoadDialogFragment.newInstance(getResources().getString(R.string.txt_load_create_appeal));
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

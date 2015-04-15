package com.zeus.android.mydeputy.app.deputy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zeus.android.mydeputy.app.BaseActivity;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.api.request.QuizCreateRequest;
import com.zeus.android.mydeputy.app.fragment.LoadDialogFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by admin on 1/26/15.
 */
public class QuizCreateActivity extends BaseActivity implements View.OnClickListener, RequestManager.OnResponseListener {

    public static final String TAG = QuizCreateActivity.class.getSimpleName();
    public static final String LOAD = "load";

    private Toolbar toolbar;

    private ImageButton btnAddVariant;

    private EditText editTitle;
    private EditText editText;
    private EditText editVariant;

    protected LinearLayout variantContainer;

    private int variantCount;

    private FragmentManager fragmentManager;

    private LoadDialogFragment loadDialogFragment;
    private boolean loadDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deputy_main_quiz_create);

        editText = (EditText) findViewById(R.id.deputy_quiz_new_edit_text);
        editTitle = (EditText) findViewById(R.id.deputy_quiz_new_edit_title);
        editVariant = (EditText) findViewById(R.id.deputy_quiz_new_edit_variant);

        btnAddVariant = (ImageButton) findViewById(R.id.deputy_quiz_new_btn_add_variant);

        variantContainer = (LinearLayout) findViewById(R.id.deputy_quiz_new_variant_conteiner);

        btnAddVariant.setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState != null){
            loadDialog = savedInstanceState.getBoolean(LOAD);
            if (loadDialog){
                showLoadDialog();
            }
        }

        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.main_dark));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(LOAD, loadDialog);
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

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.deputy_quiz_new_btn_add_variant:

                if(editVariant.getText().toString().isEmpty()){
                    Toast.makeText(this, getResources().getString(R.string.txt_quiz_variant_error), Toast.LENGTH_SHORT).show();
                }else{
                    // Dynamically add edit text
                    String variantText = editVariant.getText().toString();
                    EditText variant = (EditText)getLayoutInflater().inflate(R.layout.simple_element_edittext, null);
                    variant.setText(variantText);
                    variant.setId(variantCount);
                    variantContainer.addView(variant);
                    editVariant.getText().clear();
                    variantCount++;
                }
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.deputy_quiz_new_menu, menu);
        return true;
    }

    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type) {
            case RequestManager.REQUEST_QUIZ_CREATE:
                dismissLoadDialog();
                finish();
                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {
        switch (type) {
            case RequestManager.REQUEST_QUIZ_CREATE:
                dismissLoadDialog();
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.create_quiz:

                final String title = editTitle.getText().toString();
                final String text = editText.getText().toString();

                if(title.isEmpty()){
                    Toast.makeText(this, getResources().getString(R.string.txt_quiz_title_error), Toast.LENGTH_SHORT).show();
                }else if(text.isEmpty()){
                    Toast.makeText(this, getResources().getString(R.string.txt_quiz_text_error), Toast.LENGTH_SHORT).show();
                }else if(variantCount < 2){
                    Toast.makeText(this, getResources().getString(R.string.txt_quiz_variant_number_error), Toast.LENGTH_SHORT).show();
                }else {

                    // Open Date picker dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    final DatePicker picker = new DatePicker(this);
                    picker.setCalendarViewShown(false);

                    builder.setTitle(getResources().getString(R.string.txt_quiz_end_date));
                    builder.setView(picker);
                    builder.setNegativeButton(getResources().getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton(getResources().getString(R.string.txt_quiz_create), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int day = picker.getDayOfMonth();
                            int month = picker.getMonth();
                            int year = picker.getYear();

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, day);


                            List<String> variants = new ArrayList<String>();
                            for(int i = 0; i < variantCount; i++){
                                String variant = ((EditText)variantContainer.findViewById(i)).getText().toString().trim();
                                variants.add(variant);
                            }

                            sendQuizCreateRequest((calendar.getTimeInMillis()/1000), text, title, variants);
                        }
                    });
                    builder.show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendQuizCreateRequest(long data, String text, String title, List<String> variants){
        QuizCreateRequest request = new QuizCreateRequest();
        request.setHash(getPreferencesManager().getPasswordHash());
        request.setEmail(getPreferencesManager().getEmail());
        request.setTitle(title.trim());
        request.setText(text.trim());
        request.setEnddate(data);
        request.setVariants(variants);

        getRequestManager().sendRequest(request, RequestManager.QUIZ_CREATE_URL, TAG, RequestManager.REQUEST_QUIZ_CREATE);

        showLoadDialog();
    }

    private void showLoadDialog(){
        loadDialog = true;
        if (loadDialogFragment == null) {
            loadDialogFragment = LoadDialogFragment.newInstance(getResources().getString(R.string.txt_load_create_quiz));
            loadDialogFragment.setRetainInstance(true);
            loadDialogFragment.setCancelable(false);
            loadDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Base_Theme_AppCompat_Light_Dialog_FixedSize);
        }
        loadDialogFragment.show(fragmentManager, "dialog");
    }

    private void dismissLoadDialog() {
        if (loadDialogFragment != null) {
            loadDialogFragment.dismiss();
            loadDialog = false;
        }
    }
}
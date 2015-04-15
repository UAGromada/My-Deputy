package com.zeus.android.mydeputy.app.citizen;

import com.google.gson.Gson;
import com.zeus.android.mydeputy.app.BaseActivity;
import com.zeus.android.mydeputy.app.ui.LoadLayout;
import com.zeus.android.mydeputy.app.util.HashGenerator;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.model.Quiz;
import com.zeus.android.mydeputy.app.api.request.QuizSingleRequest;
import com.zeus.android.mydeputy.app.api.request.QuizVoteRequest;
import com.zeus.android.mydeputy.app.api.response.QuizSingleResponse;
import com.zeus.android.mydeputy.app.fragment.LoadDialogFragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

public class QuizSingleVoteActivity extends BaseActivity implements RequestManager.OnResponseListener, View.OnClickListener {

    public static final String TAG = QuizSingleVoteActivity.class.getSimpleName();
    public static final String QUIZ_ID = "quiz_id";
    public static final String QUIZ_POSITION = "quizPosition";
    public static final String LOAD = "load";
    public static final String QUIZ_READY = "quiz_ready";

	private Quiz mQuiz;
	private TextView mQuizHeaderView;
    private TextView mQuizText;
    private RadioGroup variantContainer;
    private Toolbar toolbar;
    private LoadLayout loadLayout;

    private int quizId;
    private int quizPosition;

    private LoadDialogFragment loadDialogFragment;
    private boolean loadDialog = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.citizen_main_quiz_single);

        quizId = getIntent().getExtras().getInt(QUIZ_ID);
        quizPosition = getIntent().getExtras().getInt(QUIZ_POSITION);

        mQuizHeaderView = (TextView) findViewById(R.id.deputy_single_quiz_header);
        mQuizText = (TextView) findViewById(R.id.deputy_single_quiz_text);
        variantContainer = (RadioGroup) findViewById(R.id.citizen_quiz_single_response_container);
        loadLayout = (LoadLayout) findViewById(R.id.error_layout);
        loadLayout.setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        mQuiz = getLocalManager().getCurrentDeputy().getQuizList().get(quizPosition);

        mQuizHeaderView.setText(mQuiz.getTitle());
        mQuizText.setText(mQuiz.getText());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mQuiz.getTitle());

        if (savedInstanceState != null){
            loadDialog = savedInstanceState.getBoolean(LOAD);
            if (loadDialog){
                showLoadDialog();
            }
        }else{
            sendQuizSingleRequest();
            quizVariantsCreator(mQuiz.getVariants());
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.load_retry:
                sendQuizSingleRequest();
                break;
        }
    }

    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type) {
            case RequestManager.REQUEST_QUIZ_VOTE:
                dismissLoadDialog();
                finish();
                break;
            case RequestManager.REQUEST_QUIZ_SINGLE:
                QuizSingleResponse quizSingleResponse = new Gson().fromJson(response.toString(), QuizSingleResponse.class);
                mQuiz = quizSingleResponse.getPoll();
                getLocalManager().getCurrentDeputy().getQuizList().set(quizPosition, mQuiz);
                quizVariantsCreator(mQuiz.getVariants());
                loadLayout.fade(true);
                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {
        switch (type) {
            case RequestManager.REQUEST_QUIZ_VOTE:
                dismissLoadDialog();
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                break;
            case RequestManager.REQUEST_QUIZ_SINGLE:
                loadLayout.setErrorState(getResources().getString(R.string.load_error));
                break;
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
                if (variantContainer.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(this, getResources().getString(R.string.txt_error_all), Toast.LENGTH_SHORT).show();
                } else {
                    showLoadDialog();
                    sendQuizVoteRequest(variantContainer.getCheckedRadioButtonId());
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendQuizSingleRequest(){
        loadLayout.setVisibility(View.VISIBLE);
        loadLayout.setLoadState(getResources().getString(R.string.txt_load_quiz));

        QuizSingleRequest request =new QuizSingleRequest();
        request.setHash(new HashGenerator().hash(getPreferencesManager().getPassword()));
        request.setEmail(getPreferencesManager().getEmail());
        request.setPollId(quizId);

        getRequestManager().sendRequest(request, RequestManager.QUIZ_SINGLE_URL, TAG, RequestManager.REQUEST_QUIZ_SINGLE);
    }

    private void sendQuizVoteRequest(int variantId){
        QuizVoteRequest request =new QuizVoteRequest();
        request.setHash(getPreferencesManager().getPasswordHash());
        request.setEmail(getPreferencesManager().getEmail());
        request.setPollId(quizId);
        request.setVariantId(variantId);

        getRequestManager().sendRequest(request, RequestManager.QUIZ_VOTE_URL, TAG, RequestManager.REQUEST_QUIZ_VOTE);
    }

    private void quizVariantsCreator(List<Quiz.Variant> variants){
        for (Quiz.Variant variant: variants){

            RadioButton radio = (RadioButton)getLayoutInflater().inflate(R.layout.simple_element_radio_button, null);
            radio.setText(variant.getText());
            radio.setId(variant.getVariantId());
            variantContainer.addView(radio);
            radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    variantContainer.check(view.getId());
                }
            });
        }
    }

    private void showLoadDialog(){
        loadDialog = true;
        if (loadDialogFragment == null) {
            loadDialogFragment = LoadDialogFragment.newInstance(getResources().getString(R.string.txt_load_create_appeal));
            loadDialogFragment.setRetainInstance(true);
            loadDialogFragment.setCancelable(false);
            loadDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Base_Theme_AppCompat_Light_Dialog_FixedSize);
        }
        loadDialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    private void dismissLoadDialog(){
        if (loadDialogFragment != null ){
            loadDialogFragment.dismiss();
        }
        loadDialog = false;
    }
}

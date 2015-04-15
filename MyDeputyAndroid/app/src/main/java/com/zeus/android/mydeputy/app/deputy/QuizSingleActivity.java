package com.zeus.android.mydeputy.app.deputy;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zeus.android.mydeputy.app.BaseActivity;
import com.zeus.android.mydeputy.app.ui.ErrorLayout;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.model.Quiz;
import com.zeus.android.mydeputy.app.api.request.QuizSingleRequest;
import com.zeus.android.mydeputy.app.api.response.QuizSingleResponse;

import org.json.JSONObject;

/**
 * Created by admin on 1/26/15.
 */
public class QuizSingleActivity extends BaseActivity implements RequestManager.OnResponseListener, View.OnClickListener {

    public static final String TAG = QuizSingleActivity.class.getSimpleName();
    public static final String QUIZ_ID = "quiz_id";

    private Toolbar mToolbar;
    private TextView title;
    private TextView info;
    private ListView quizResponse;
    private ErrorLayout errorLayout;

    private Quiz mQuiz;
    private QuizResponseAdapter adapter;

    private  int quizID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deputy_main_quiz_single);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        title = (TextView) findViewById(R.id.deputy_quiz_full_info_title);
        info = (TextView) findViewById(R.id.deputy_quiz_full_info_info);
        quizResponse = (ListView) findViewById(R.id.deputy_quiz_full_info_list);
        errorLayout = (ErrorLayout) findViewById(R.id.error_layout);
        errorLayout.setOnClickListener(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        quizID = getIntent().getExtras().getInt(QUIZ_ID);
        mQuiz = getLocalManager().getCurrentDeputy().getQuizById(quizID);
        if(mQuiz.getVariants().size() != 0) {
            title.setText(mQuiz.getTitle());
            info.setText(mQuiz.getText());
            adapter = new QuizResponseAdapter(mQuiz);
            quizResponse.setAdapter(adapter);
        }

        sendQuizSingleRequest();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type) {
            case RequestManager.REQUEST_QUIZ_SINGLE:
                QuizSingleResponse quizSingleResponse = new Gson().fromJson(response.toString(), QuizSingleResponse.class);
                mQuiz = quizSingleResponse.getPoll();

                title.setText(mQuiz.getTitle());
                info.setText(mQuiz.getText());

                getLocalManager().getCurrentDeputy().getQuizById(quizID).setVariants(mQuiz.getVariants());

                if (adapter == null) {
                    adapter = new QuizResponseAdapter(mQuiz);
                    quizResponse.setAdapter(new QuizResponseAdapter(mQuiz));
                }else {
                    adapter.setQuiz(mQuiz);
                }

                errorLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {
        switch (type) {
            case RequestManager.REQUEST_QUIZ_SINGLE:
                errorLayout.setVisibility(View.VISIBLE);
                break;
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

    public void sendQuizSingleRequest(){
        QuizSingleRequest request =new QuizSingleRequest();
        request.setHash(getPreferencesManager().getPasswordHash());
        request.setEmail(getPreferencesManager().getEmail());
        request.setPollId(quizID);
        getRequestManager().sendRequest(request, RequestManager.QUIZ_SINGLE_URL, TAG, RequestManager.REQUEST_QUIZ_SINGLE);
    }

    private class QuizResponseAdapter extends BaseAdapter {
        private Quiz mQuiz;
        private int allVotes;

        public QuizResponseAdapter(Quiz quiz) {
            super();
            mQuiz=quiz;
            allVotes = 0;
            for (Quiz.Variant variant: mQuiz.getVariants()){
                allVotes += variant.getVotes();
            }
        }

        public void setQuiz(Quiz quiz){
            mQuiz = quiz;
            notifyDataSetChanged();
        }

        public int getCount() {
            return mQuiz.getVariants().size();
        }

        public Object getItem(int position) {
            return mQuiz.getVariants().get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder=null;

            if(convertView==null){

                // inflate the layout
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.deputy_main_quiz_single_item, parent, false);

                // well set up the ViewHolder
                holder = new Holder(convertView);

            }else{
                // we've just avoided calling findViewById() on resource everytime
                // just use the viewHolder
                holder = (Holder) convertView.getTag();
            }

            Quiz.Variant quizVariant=(Quiz.Variant)getItem(position);


            if(quizVariant != null){
                holder.mHeader.setText(quizVariant.getText());
                holder.bar.setProgress(quizVariant.getVotes());
                holder.mCount.setText("("+quizVariant.getVotes()+")");
            }

            return convertView;
        }

        private class Holder {
            public TextView mHeader;
            public TextView mCount;
            public ProgressBar bar;

            public Holder(View container) {
                mHeader=(TextView)container.findViewById(R.id.deputy_quiz_full_info_item_txt);
                mCount=(TextView)container.findViewById(R.id.deputy_quiz_full_info_item_count);
                bar = (ProgressBar)container.findViewById(R.id.deputy_quiz_full_info_item_progressBar);
                bar.setMax(allVotes);

                // store the holder with the view.
                container.setTag(this);
            }
        }

    }
}

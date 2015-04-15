package com.zeus.android.mydeputy.app.citizen.main_fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.ui.ErrorLayout;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.model.Quiz;
import com.zeus.android.mydeputy.app.api.request.QuizListRequest;
import com.zeus.android.mydeputy.app.api.response.QuizListResponse;
import com.zeus.android.mydeputy.app.citizen.QuizSingleVoteActivity;
import com.zeus.android.mydeputy.app.deputy.QuizSingleActivity;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by admin on 2/13/15.
 */
public class QuizFragment extends BaseFragment implements
        RequestManager.OnResponseListener,
        SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener{

    public static final String TAG = QuizFragment.class.getSimpleName();

    private ListView mQuizListView;
    private List<Quiz> quizList;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ErrorLayout errorLayout;

    private Activity activity;

    private QuizAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.citizen_main_quiz, container, false);

        mQuizListView=(ListView)v.findViewById(R.id.citizen_main_quiz_list);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.secondary, R.color.main_light, R.color.main);
        swipeRefreshLayout.setOnRefreshListener(this);
        errorLayout = (ErrorLayout) v.findViewById(R.id.error_layout);
        errorLayout.setOnClickListener(this);

        quizList = getLocalManager().getCurrentDeputy().getQuizList();

        adapter = new QuizAdapter(quizList);
        mQuizListView.setAdapter(adapter);

        activity.setTitle(activity.getResources().getString(R.string.title_deputy_quiz));

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getRequestManager().addListener(this);
        sendQuizListRequest();
    }

    @Override
    public void onStop() {
        super.onStop();
        getRequestManager().removeListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type){
            case RequestManager.REQUEST_QUIZ_LIST:
                QuizListResponse quizListResponse = new Gson().fromJson(response.toString(), QuizListResponse.class);
                quizList = quizListResponse.getQuizList();
                adapter.setQuizList(quizList);

                swipeRefreshLayout.setRefreshing(false);
                errorLayout.setVisibility(View.GONE);

                getLocalManager().getCurrentDeputy().setQuizList(quizList);
                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {
        switch (type){
            case RequestManager.REQUEST_QUIZ_LIST:
                swipeRefreshLayout.setRefreshing(false);
                errorLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onRefresh() {
        sendQuizListRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.error_retry:
                sendQuizListRequest();
                break;
        }
    }

    protected void sendQuizListRequest(){
        QuizListRequest request =new QuizListRequest();
        request.setHash(getPreferencesManager().getPasswordHash());
        request.setEmail(getPreferencesManager().getEmail());
        request.setDeputyId(getPreferencesManager().getDeputyId());
        request.setEnded(QuizListRequest.STATE_ACTIVE);

        getRequestManager().sendRequest(request, RequestManager.QUIZ_LIST_URL, TAG, RequestManager.REQUEST_QUIZ_LIST);
    }

    public class QuizAdapter extends BaseAdapter {

        private List<Quiz> quizs;

        public QuizAdapter(List<Quiz> list) {
            this.quizs = list;
        }

        private void setQuizList(List<Quiz> quizs){
            this.quizs = quizs;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return quizs.size();
        }

        @Override
        public Object getItem(int position) {
            return quizs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = null;

            if (convertView == null) {

                // inflate the layout
                LayoutInflater inflater = activity.getLayoutInflater();
                convertView = inflater.inflate(R.layout.simple_item_sigle_line, parent, false);

                // well set up the ViewHolder
                holder = new Holder(convertView);

            } else {
                // we've just avoided calling findViewById() on resource everytime
                // just use the viewHolder
                holder = (Holder) convertView.getTag();
            }

            final Quiz quiz = (Quiz) getItem(position);

            if (quiz != null) {
                holder.mHeader.setText(quiz.getTitle());
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Class c = null;
                    if (quiz.isVoted()) {
                        c = QuizSingleActivity.class;
                    } else {
                        c = QuizSingleVoteActivity.class;
                    }
                    Intent i = new Intent(activity, c);
                    i.putExtra(QuizSingleVoteActivity.QUIZ_ID, quiz.getQuizId());
                    i.putExtra(QuizSingleVoteActivity.QUIZ_POSITION, position);
                    startActivity(i);
                }
            });

            return convertView;
        }


        private class Holder {
            public TextView mHeader;

            public Holder(View container) {
                mHeader = (TextView) container.findViewById(R.id.item_title);

                // store the holder with the view.
                container.setTag(this);
            }
        }
    }

}

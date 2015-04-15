package com.zeus.android.mydeputy.app.deputy.fragments;

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
import com.zeus.android.mydeputy.app.util.HashGenerator;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.model.Quiz;
import com.zeus.android.mydeputy.app.api.request.QuizListRequest;
import com.zeus.android.mydeputy.app.api.response.QuizListResponse;
import com.zeus.android.mydeputy.app.deputy.QuizSingleActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by admin on 1/26/15.
 */
public class QuizInProcessFragment extends BaseFragment implements
        RequestManager.OnResponseListener,
        SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener{

    public static final String TAG = QuizInProcessFragment.class.getSimpleName();

    public List<Quiz> quizList;
    private ListView quizListView;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ErrorLayout errorLayout;

    private QuizAdapter adapter;

    private Activity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.deputy_main_quiz_in_process, container, false);

        quizListView =(ListView) view.findViewById(R.id.deputy_quiz_list_process);
        errorLayout = (ErrorLayout) view.findViewById(R.id.error_layout);
        errorLayout.setOnClickListener(this);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.secondary, R.color.main_light, R.color.main);
        swipeRefreshLayout.setOnRefreshListener(this);

        adapter = new QuizAdapter(filter(getLocalManager().getCurrentDeputy().getQuizList()));
        quizListView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
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
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type) {
            case RequestManager.REQUEST_QUIZ_LIST:
                QuizListResponse quizListResponse = new Gson().fromJson(response.toString(), QuizListResponse.class);
                quizList = quizListResponse.getQuizList();
                getLocalManager().getCurrentDeputy().setQuizList(quizList);

                adapter.setDeputyList(filter(quizList));

                swipeRefreshLayout.setRefreshing(false);
                errorLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {
        switch (type) {
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

    public void sendQuizListRequest(){
        QuizListRequest request =new QuizListRequest();
        request.setHash(new HashGenerator().hash(getPreferencesManager().getPassword()));
        request.setEmail(getPreferencesManager().getEmail());
        request.setDeputyId(getPreferencesManager().getDeputyId());
        request.setEnded(QuizListRequest.STATE_ALL);

        getRequestManager().sendRequest(request, RequestManager.QUIZ_LIST_URL, TAG, RequestManager.REQUEST_QUIZ_LIST);
    }

    public List<Quiz> filter(List<Quiz> quizList){
        Calendar calendar = Calendar.getInstance();
        this.quizList = new ArrayList<>();
        for (Quiz quiz: quizList){
            if (quiz.getEndDate() * 1000 > calendar.getTimeInMillis()){
                this.quizList.add(quiz);
            }
        }
        return this.quizList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.error_retry:
                sendQuizListRequest();
                break;
        }
    }

    public class QuizAdapter extends BaseAdapter {

        private List<Quiz> deputyList;

        public QuizAdapter(List<Quiz> list) {
            this.deputyList = list;
        }

        public void setDeputyList(List<Quiz> list){
            this.deputyList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return deputyList.size();
        }

        @Override
        public Object getItem(int position) {
            return deputyList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder=null;

            if(convertView==null){

                // inflate the layout
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.simple_element_single_item, parent, false);

                // well set up the ViewHolder
                holder = new Holder(convertView);

            }else{
                // we've just avoided calling findViewById() on resource everytime
                // just use the viewHolder
                holder = (Holder) convertView.getTag();
            }

            Quiz quiz = (Quiz) getItem(position);

            if (quiz != null) {
                holder.mHeader.setText(quiz.getTitle());
            }


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), QuizSingleActivity.class);
                    i.putExtra(QuizSingleActivity.QUIZ_ID, (quizList.get(position).getQuizId()));
                    startActivity(i);
                }
            });

            return convertView;
        }


        private class Holder {
            public TextView mHeader;

            public Holder(View container) {
                mHeader=(TextView)container.findViewById(R.id.deputy_single_item_txt);

                container.setTag(this);
            }
        }
    }
}

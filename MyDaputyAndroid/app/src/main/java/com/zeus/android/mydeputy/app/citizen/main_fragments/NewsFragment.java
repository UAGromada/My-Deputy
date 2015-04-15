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
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.request.BaseListRequest;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.model.News;
import com.zeus.android.mydeputy.app.api.response.NewsListResponse;
import com.zeus.android.mydeputy.app.citizen.NewsSingleActivity;
import com.zeus.android.mydeputy.app.ui.ErrorLayout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2/13/15.
 */
public class NewsFragment extends BaseFragment implements
        RequestManager.OnResponseListener,
        SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener{

    public static final String TAG = NewsFragment.class.getSimpleName();

    private List<News> newsList;
    private ListView newsListView;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ErrorLayout errorLayout;

    private Activity activity;

    private NewsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.citizen_main_news, container, false);

        newsList = getLocalManager().getCurrentDeputy().getNewsList();

        newsListView =(ListView) v.findViewById(R.id.deputy_news_list);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.secondary, R.color.main_light, R.color.main);
        swipeRefreshLayout.setOnRefreshListener(this);
        errorLayout = (ErrorLayout) v.findViewById(R.id.error_layout);
        errorLayout.setOnClickListener(this);

        adapter = new NewsAdapter(newsList);
        newsListView.setAdapter(adapter);

        activity.setTitle(activity.getResources().getString(R.string.title_deputy_news));

        return v;
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
        sendNewsListRequest();
    }

    @Override
    public void onStop() {
        super.onStop();
        getRequestManager().removeListener(this);
    }

    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type){
            case RequestManager.REQUEST_NEWS_LIST:
                NewsListResponse quizListResponse = new Gson().fromJson(response.toString(), NewsListResponse.class);
                newsList = quizListResponse.getNewsList();
                getLocalManager().getCurrentDeputy().setNewsList(newsList);

                adapter.setNewsList(newsList);

                swipeRefreshLayout.setRefreshing(false);
                errorLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {
        switch (type){
            case RequestManager.REQUEST_NEWS_LIST:
                swipeRefreshLayout.setRefreshing(false);
                errorLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void sendNewsListRequest(){
        BaseListRequest request = new BaseListRequest();
        request.setHash(getPreferencesManager().getPasswordHash());
        request.setEmail(getPreferencesManager().getEmail());
        request.setDeputyId(getPreferencesManager().getDeputyId());

        getRequestManager().sendRequest(request, RequestManager.NEWS_LIST_URL, TAG, RequestManager.REQUEST_NEWS_LIST);
    }


    @Override
    public void onRefresh() {
        sendNewsListRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.error_retry:
                sendNewsListRequest();
                break;
        }
    }

    public class NewsAdapter extends BaseAdapter {

        private List<News> newses;

        public NewsAdapter(List<News> list) {
            this.newses = list;
        }

        public void setNewsList(List<News> list){
            this.newses = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return newses.size();
        }

        @Override
        public Object getItem(int position) {
            return newses.get(position);
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
                LayoutInflater inflater = activity.getLayoutInflater();
                convertView = inflater.inflate(R.layout.simple_item_two_lines_date, parent, false);

                // well set up the ViewHolder
                holder = new Holder(convertView);

            }else{
                // we've just avoided calling findViewById() on resource everytime
                // just use the viewHolder
                holder = (Holder) convertView.getTag();
            }

            final News news= (News) getItem(position);

            if(news != null) {
                holder.mHeader.setText(news.getTitle());
                holder.mText.setText(news.getText());
                SimpleDateFormat df = new SimpleDateFormat("dd MMM yy");
                String date = df.format(new Date(news.getCreated()*1000));
                holder.mDate.setText(date);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent newsIntent=new Intent(getActivity(),NewsSingleActivity.class);
                        newsIntent.putExtra(NewsSingleActivity.NEWS_KEY, news.getNewsId());
                        startActivity(newsIntent);
                    }
                });
            }

            return convertView;
        }


        private class Holder {
            public TextView mHeader;
            public TextView mText;
            public TextView mDate;

            public Holder(View container) {
                mHeader =(TextView)container.findViewById(R.id.item_title);
                mText =(TextView)container.findViewById(R.id.item_text);
                mDate =(TextView)container.findViewById(R.id.item_data);
                // store the holder with the view.
                container.setTag(this);
            }
        }
    }
}

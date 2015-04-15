package com.zeus.android.mydeputy.app.deputy.main_fragments;

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
import com.melnykov.fab.FloatingActionButton;
import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.request.BaseListRequest;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.model.News;
import com.zeus.android.mydeputy.app.api.response.NewsListResponse;
import com.zeus.android.mydeputy.app.deputy.NewsCreateActivity;
import com.zeus.android.mydeputy.app.deputy.NewsSingleActivity;
import com.zeus.android.mydeputy.app.ui.ErrorLayout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2/16/15.
 */
public class NewsFragment extends BaseFragment implements
        RequestManager.OnResponseListener,
        SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener{

    public static final String TAG = NewsFragment.class.getSimpleName();

    private FloatingActionButton mAddNewsButton;
    private ListView newsListView;
    private List<News> newsList;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ErrorLayout errorLayout;

    private NewsAdapter adapter;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.deputy_main_news, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.secondary, R.color.main_light, R.color.main);
        swipeRefreshLayout.setOnRefreshListener(this);
        errorLayout = (ErrorLayout) view.findViewById(R.id.error_layout);
        errorLayout.setOnClickListener(this);

        newsListView =(ListView) view.findViewById(R.id.deputy_part_news_list);
        registerForContextMenu(newsListView);

        mAddNewsButton=(FloatingActionButton) view.findViewById(R.id.deputy_part_news_add_button);
        mAddNewsButton.setOnClickListener(new AddNewsClickListener());

        newsList = getLocalManager().getCurrentDeputy().getNewsList();
        adapter = new NewsAdapter(newsList);
        newsListView.setAdapter(adapter);

        activity.setTitle(activity.getResources().getString(R.string.title_deputy_news));

        return view;
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type) {
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
        switch (type) {
            case RequestManager.REQUEST_NEWS_LIST:
                swipeRefreshLayout.setRefreshing(false);
                errorLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onRefresh() {
        sendNewsListRequest();
    }

    private void sendNewsListRequest(){
        BaseListRequest request = new BaseListRequest();
        request.setHash(getPreferencesManager().getPasswordHash());
        request.setEmail(getPreferencesManager().getEmail());
        request.setDeputyId(getPreferencesManager().getDeputyId());

        getRequestManager().sendRequest(request, RequestManager.NEWS_LIST_URL, TAG, RequestManager.REQUEST_NEWS_LIST);
    }

    @Override
    public void onClick(View v) {
        sendNewsListRequest();
    }


    private class AddNewsClickListener implements View.OnClickListener {
        public void onClick(View v) {
            Intent i = new Intent(activity, NewsCreateActivity.class);
            activity.startActivity(i);
        }
    }

    public class NewsAdapter extends BaseAdapter {

        private List<News> newsList;

        public NewsAdapter(List<News> news) {
            newsList = news;
        }

        public void setNewsList(List<News> news){
            this.newsList = news;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return newsList.size();
        }

        @Override
        public Object getItem(int position) {
            return newsList.get(position);
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
                        Intent i = new Intent(activity, NewsSingleActivity.class);
                        i.putExtra(NewsSingleActivity.NEWS_ID, news.getNewsId());
                        activity.startActivity(i);
                    }
                });
                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        activity.openContextMenu(v);
                        return true;
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

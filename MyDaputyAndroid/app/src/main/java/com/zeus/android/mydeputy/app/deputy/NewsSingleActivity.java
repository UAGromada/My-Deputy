package com.zeus.android.mydeputy.app.deputy;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zeus.android.mydeputy.app.BaseActivity;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.model.News;
import com.zeus.android.mydeputy.app.api.request.NewsSingleRequest;
import com.zeus.android.mydeputy.app.api.response.NewsSingleResponse;
import com.zeus.android.mydeputy.app.ui.ErrorLayout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2/17/15.
 */
public class NewsSingleActivity extends BaseActivity implements
        RequestManager.OnResponseListener,
        View.OnClickListener{

    public static final String TAG = NewsSingleActivity.class.getSimpleName();
    public static final String NEWS_ID = "news_id";

    private Toolbar mToolbar;

    private  int newsID;

    private LinearLayout commentContainer;
    private LinearLayout commentLayout;
    private TextView title;
    private TextView text;
    private ErrorLayout errorLayout;

    private List<News.NewsComment> newsCommentList;

    private News news;
    private int newsIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deputy_main_news_single);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newsID = getIntent().getExtras().getInt(NEWS_ID);

        commentContainer = (LinearLayout) findViewById(R.id.deputy_news_full_info_comment_container);
        commentLayout = (LinearLayout) findViewById(R.id.deputy_news_comments_container);
        title = (TextView) findViewById(R.id.deputy_news_full_info_title);
        text = (TextView) findViewById(R.id.deputy_news_full_info_text);
        errorLayout = (ErrorLayout) findViewById(R.id.error_layout);
        errorLayout.setOnClickListener(this);

        news = getLocalManager().getCurrentDeputy().getNewsById(newsID);
        newsIndex = getLocalManager().getCurrentDeputy().getNewsList().indexOf(news);

        getSupportActionBar().setTitle(news.getTitle());
        sendNewsSingleRequest();

        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(R.color.main_dark));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type){
            case RequestManager.REQUEST_NEWS_SINGLE:

                NewsSingleResponse newsSingleResponse = new Gson().fromJson(response.toString(), NewsSingleResponse.class);
                news = newsSingleResponse.getNews();

                title.setText(news.getTitle());
                text.setText(news.getText());

                newsCommentList = news.getComments();
                if (newsCommentList.size() != 0) {
                    commentLayout.setVisibility(View.VISIBLE);
                    new CommentAdapter(newsCommentList, commentContainer).addViews();
                }

                errorLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {
        switch (type){
            case RequestManager.REQUEST_NEWS_SINGLE:
                errorLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void sendNewsSingleRequest(){
        NewsSingleRequest request = new NewsSingleRequest();
        request.setHash(getPreferencesManager().getPasswordHash());
        request.setEmail(getPreferencesManager().getEmail());
        request.setNewsId(newsID);

        getRequestManager().sendRequest(request, RequestManager.NEWS_SINGLE_URL, TAG, RequestManager.REQUEST_NEWS_SINGLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.error_retry:
                sendNewsSingleRequest();
                break;
        }
    }

    /**
     *  Class that generate CommentView  and add it to Container
     *  @author Rosty Vasiukov
     */
    public class CommentAdapter{

        private List<News.NewsComment> newsCommentList;
        private LinearLayout parent;

        public CommentAdapter(List<News.NewsComment> news, LinearLayout parent) {

            this.newsCommentList = news;
            this.parent = parent;
        }

        public void addViews(){
            for (News.NewsComment comment: newsCommentList){
                addView(comment);
            }
        }

        private void addView(News.NewsComment comment){

            LinearLayout commentView = (LinearLayout) getLayoutInflater().inflate(R.layout.simple_item_two_lines_date, null);

            TextView mName =(TextView)commentView.findViewById(R.id.item_title);
            TextView mText =(TextView)commentView.findViewById(R.id.item_text);
            TextView mDate =(TextView)commentView.findViewById(R.id.item_data);
            FrameLayout divider = (FrameLayout) commentView.findViewById(R.id.item_divider);
            divider.setVisibility(View.VISIBLE);

            News.NewsComment newsComment = comment;

            if(newsComment != null) {
                mName.setText(newsComment.getAuthorName());
                mText.setText(newsComment.getCommentText());
                SimpleDateFormat df = new SimpleDateFormat("dd MMM yy");
                String date = df.format(new Date(newsComment.getCreated()*1000));
                mDate.setText(date);
            }

            parent.addView(commentView);
        }

    }
}

package com.zeus.android.mydeputy.app.api.response;

import com.zeus.android.mydeputy.app.model.News;

import java.util.List;

/**
 * Created by admin on 2/17/15.
 */
public class NewsListResponse extends BaseResponse {

    private List<News> news_list;

    public List<News> getNewsList() {
        return news_list;
    }
}

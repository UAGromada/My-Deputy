package com.zeus.android.mydeputy.app.api.request;

/**
 * Created by admin on 2/19/15.
 */
public class NewsAddCommentRequest extends NewsSingleRequest {

    private String text;

    public void setText(String text) {
        this.text = text;
    }
}

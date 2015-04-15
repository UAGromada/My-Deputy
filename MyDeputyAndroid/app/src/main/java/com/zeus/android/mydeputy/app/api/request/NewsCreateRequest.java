package com.zeus.android.mydeputy.app.api.request;

/**
 * Created by admin on 2/17/15.
 */
public class NewsCreateRequest extends BaseRequest {

    private String text;
    private String title;

    public void setText(String text) {
        this.text = text;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

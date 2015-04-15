package com.zeus.android.mydeputy.app.api.request;

/**
 * Created by admin on 2/11/15.
 */
public class AppealCreateRequest extends BaseListRequest {

    private String message;
    private String title;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

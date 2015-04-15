package com.zeus.android.mydeputy.app.api.request;

/**
 * Created by admin on 2/16/15.
 */
public class QuizListRequest extends BaseListRequest {

    public static final int STATE_ACTIVE = 0;
    public static final int STATE_ALL = 1;
    public static final int STATE_ENDED = 2;
    private int ended;

    public void setEnded(int ended) {
        this.ended = ended;
    }
}

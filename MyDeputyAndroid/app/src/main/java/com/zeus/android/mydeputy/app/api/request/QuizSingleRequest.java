package com.zeus.android.mydeputy.app.api.request;

/**
 * Created by admin on 2/16/15.
 */
public class QuizSingleRequest extends BaseRequest {

    private int poll_id;

    public void setPollId(int poll_id){
        this.poll_id =poll_id;
    }
}

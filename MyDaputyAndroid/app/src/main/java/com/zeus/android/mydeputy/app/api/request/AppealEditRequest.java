package com.zeus.android.mydeputy.app.api.request;

/**
 * Created by admin on 2/17/15.
 */
public class AppealEditRequest extends BaseRequest {

    private int appeal_id;
    private Params params;

    public void setAppeal_id(int appeal_id) {
        this.appeal_id = appeal_id;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public static class Params{
        private int state;

        public Params(int state) {
            this.state = state;
        }
    }
}

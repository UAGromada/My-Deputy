package com.zeus.android.mydeputy.app.model;

/**
 * Created by admin on 2/2/15.
 */
public class Appeal {

    public static final int STATE_NEW = 0;
    public static final int STATE_ACCEPTED = 1;
    public static final int STATE_REJECTED = 2;

    private int id;
    private String title;
    private String message;
    private long created;
    private int state;
    private float rate;
    private int citizen_id;

    public Appeal(int appealId, String personId) {
        this.id = appealId;
    }

    public Appeal() {
    }

    public int getAppealId() {
        return id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return message;
    }

    public void setText(String text) {
        this.message = text;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }


    public int getCitizenId() {
        return citizen_id;
    }

    public void setCitizenId(int citizen_id) {
        this.citizen_id = citizen_id;
    }
}

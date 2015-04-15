package com.zeus.android.mydeputy.app.api.request;

/**
 * Created by admin on 2/11/15.
 */
public class BaseRequest {

    private String email;
    private String hash;

    public BaseRequest(String email, String hash) {
        this.email = email;
        this.hash = hash;
    }

    public BaseRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}

package com.zeus.android.mydeputy.app.api.request;

/**
 * Created by admin on 1/27/15.
 */
public class RegisterRequest extends BaseRequest {

    private int role;
    private String full_name;
    private String party_name;

    public RegisterRequest() {
    }

    public String getFullName() {
        return full_name;
    }

    public void setFullName(String full_name) {
        this.full_name = full_name;
    }

    public String getPartyName() {
        return party_name;
    }

    public void setPartyName(String party_name) {
        this.party_name = party_name;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

}

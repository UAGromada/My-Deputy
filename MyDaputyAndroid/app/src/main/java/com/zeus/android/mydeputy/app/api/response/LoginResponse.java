package com.zeus.android.mydeputy.app.api.response;

/**
 * Created by admin on 2/10/15.
 */
public class LoginResponse extends BaseResponse {

    private User user;

    public User getUser() {
        return user;
    }

    public static class User{

        private int id;
        private int role;
        private String full_name;
        private String party_name;

        public int getId() {
            return id;
        }

        public int getRole() {
            return role;
        }

        public String getFull_name() {
            return full_name;
        }

        public String getParty_name() {
            return party_name;
        }
    }
}

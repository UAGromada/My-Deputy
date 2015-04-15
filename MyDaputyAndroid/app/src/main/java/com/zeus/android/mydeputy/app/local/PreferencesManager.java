package com.zeus.android.mydeputy.app.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.zeus.android.mydeputy.app.App;
import com.zeus.android.mydeputy.app.util.HashGenerator;

/**
 * Created by admin on 2/5/15.
 */
public class PreferencesManager {

    private static final String SP_SAVE_LOGIN = "save_login";
    private static final String SP_EMAIL = "email";
    private static final String SP_PASSWORD = "password";
    private static final String SP_ROLE = "role";
    private static final String SP_DEPUTY_ID = "deputy_id";
    private static final String SP_PARTY_NAME = "party";
    private static final String SP_DEPUTY_NAME = "deputy";
    private static final String SP_USER_NAME = "user";

    private boolean saveLogin;
    private String email;
    private String password;
    private int role;
    private int deputyId;
    private String userName;
    private String deputyName;
    private String partyName;

    private SharedPreferences sharedPreferences;

    public PreferencesManager() {
        sharedPreferences = App.getInstance().getSharedPreferences(App.SP_FILE_NAME, Context.MODE_PRIVATE);
        getData();
    }

    private void getData() {
        saveLogin = sharedPreferences.getBoolean(SP_SAVE_LOGIN, false);
        email = sharedPreferences.getString(SP_EMAIL, "");
        password = sharedPreferences.getString(SP_PASSWORD, "");
        role = sharedPreferences.getInt(SP_ROLE, 0);
        deputyId = sharedPreferences.getInt(SP_DEPUTY_ID, 0);
        partyName = sharedPreferences.getString(SP_PARTY_NAME, "");
        deputyName = sharedPreferences.getString(SP_DEPUTY_NAME, "");
        userName = sharedPreferences.getString(SP_USER_NAME, "");
    }

    public void erase() {
        sharedPreferences.edit().remove(SP_SAVE_LOGIN ).apply();
        sharedPreferences.edit().remove(SP_EMAIL).apply();
        sharedPreferences.edit().remove(SP_PASSWORD ).apply();
        sharedPreferences.edit().remove(SP_ROLE ).apply();
        sharedPreferences.edit().remove(SP_DEPUTY_ID ).apply();
        sharedPreferences.edit().remove(SP_PARTY_NAME).apply();
        sharedPreferences.edit().remove(SP_USER_NAME ).apply();
        sharedPreferences.edit().remove(SP_DEPUTY_NAME ).apply();
        getData();
    }

    public void setSaveLogin(boolean saveLogin) {
        sharedPreferences.edit().putBoolean(SP_SAVE_LOGIN, saveLogin).apply();
        this.saveLogin = saveLogin;
    }

    public boolean getSaveLogin() {
        return saveLogin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        sharedPreferences.edit().putString(SP_EMAIL, email).apply();
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        sharedPreferences.edit().putString(SP_PASSWORD, password).apply();
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        sharedPreferences.edit().putInt(SP_ROLE, role).apply();
        this.role = role;
    }

    public int getDeputyId() {
        return deputyId;
    }

    public void setDeputyId(int deputyId) {
        sharedPreferences.edit().putInt(SP_DEPUTY_ID, deputyId).apply();
        this.deputyId = deputyId;
    }

    public String getPasswordHash(){
        return new HashGenerator().hash(getPassword());
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        sharedPreferences.edit().putString(SP_PARTY_NAME, partyName).apply();
        this.partyName = partyName;
    }

    public String getDeputyName() {
        return deputyName;
    }

    public void setDeputyName(String deputyName) {
        sharedPreferences.edit().putString(SP_DEPUTY_NAME, deputyName).apply();
        this.deputyName = deputyName;
    }

    public String getUserName() {
        sharedPreferences.edit().putString(SP_USER_NAME, userName).apply();
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

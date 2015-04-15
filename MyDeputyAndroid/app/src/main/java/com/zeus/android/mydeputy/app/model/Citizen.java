package com.zeus.android.mydeputy.app.model;

import java.util.List;

/**
 * Created by admin on 2/18/15.
 */
public class Citizen {

    private int id;
    private String full_name;
    private String about;
    private String photo;

    private List<Quiz> polls;
    private List<Appeal> appeals;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return full_name;
    }

    public void setFullName(String fullName) {
        this.full_name = fullName;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<Quiz> getQuizList() {
        return polls;
    }

    public void setQuizList(List<Quiz> quizList) {
        this.polls = quizList;
    }

    public List<Appeal> getAppealList() {
        return appeals;
    }

    public void setAppealList(List<Appeal> appealList) {
        this.appeals = appealList;
    }
}

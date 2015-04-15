package com.zeus.android.mydeputy.app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2/2/15.
 */
public class Deputy {

    private String full_name;
    private String photo;
    private String party_name;
    private int id;

    private String program;     // Deputy program text
    private String promises;    // Deputy promises text
    private Contacts contacts;     // Deputy's contacts

    private List<News> newsList;
    private List<Quiz> quizList;
    private List<Appeal> appealList;

    public Deputy() {
    }

    public Deputy(String name, String photo, String party_name, int id) {
        this.full_name = name;
        this.photo = photo;
        this.party_name = party_name;
        this.id = id;
    }

    public Deputy(int id) {
        this.id = id;
    }

    public String getName() {
        return full_name;
    }

    public void setName(String name) {
        this.full_name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPartyName() {
        return party_name;
    }

    public void setPartyName(String party_name) {
        this.party_name = party_name;
    }

    public int getId() {
        return id;
    }

    public List<Appeal> getAppealList() {
        if (appealList == null){
            appealList = new ArrayList<>();
        }
        return appealList;
    }

    public void setAppealList(List<Appeal> appealList) {
        this.appealList = appealList;
    }

    public List<Quiz> getQuizList() {
        if (quizList == null){
            quizList = new ArrayList<>();
        }
        return quizList;
    }

    public void setQuizList(List<Quiz> quizList) {
        this.quizList = quizList;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getPromises() {
        return promises;
    }

    public void setPromises(String promises) {
        this.promises = promises;
    }

    public Contacts getContacts() {
        return contacts;
    }

    public void setContacts(Contacts contacts) {
        this.contacts = contacts;
    }

    public List<News> getNewsList() {
        if (newsList == null){
            newsList = new ArrayList<>();
        }
        return newsList;
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
    }

    public News getNewsById(int id){
        for (News news: newsList){
            if (news.getNewsId() == id) return news;
        }
        return null;
    }

    public static class Contacts{

        private String email;
        private String phone;
        private String address;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public Quiz getQuizById(int id){
        Quiz mQuiz = null;
        for (Quiz quiz: quizList){
            if (quiz.getQuizId() == id) mQuiz =quiz;
        }
        return mQuiz;
    }
}

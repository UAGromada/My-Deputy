package com.zeus.android.mydeputy.app.api.response;

import com.zeus.android.mydeputy.app.model.Quiz;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2/16/15.
 */
public class QuizListResponse {

    private List<Quiz> polls;

    public List<Quiz> getQuizList() {
        if (polls == null){
            polls = new ArrayList<>();
        }
        return polls;
    }

    public void setQuizList(List<Quiz> quizList) {
        this.polls = quizList;
    }
}

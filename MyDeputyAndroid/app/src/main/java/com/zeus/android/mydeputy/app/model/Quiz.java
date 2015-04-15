package com.zeus.android.mydeputy.app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2/2/15.
 */
public class Quiz {

    private int id;
    private String title;
    private String text;
    private List<Variant> variants;
    private long enddate;
    private boolean voted;

    public Quiz(int quizId) {
        this.id = quizId;
    }

    public int getQuizId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Variant> getVariants() {
        if(variants == null){
            variants = new ArrayList<>();
        }
        return variants;
    }

    public void setVariants(List<Variant> variants) {

        this.variants = variants;
    }

    public long getEndDate() {
        return enddate;
    }

    public void setEndDate(long endDate) {
        this.enddate = endDate;
    }

    public boolean isVoted() {
        return voted;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }

    public static class Variant{

        private int variant_id;
        private String text;
        private int votes;

        public int getVariantId() {
            return variant_id;
        }

        public void setVariantId(int variant_id) {
            this.variant_id = variant_id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getVotes() {
            return votes;
        }

        public void setVotes(int votes) {
            this.votes = votes;
        }
    }
}

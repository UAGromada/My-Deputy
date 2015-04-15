package com.zeus.android.mydeputy.app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 1/30/15.
 */
public class News {

    private int id;
    private String title;
    private String text;
    private long created;
    private List<NewsComment> comments;

    public News(int id) {
        this.id = id;
    }

    public int getNewsId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public long getCreated() {
        return created;
    }

    public List<NewsComment> getComments() {
        return comments;
    }

    public void addNewsComment(NewsComment newsComment) {
        if (comments == null){
            comments = new ArrayList<>();
        }
        comments.add(newsComment);
    }

    public static class NewsComment {

        private int id;
        private long created;
        private String creator_name;
        private String text;

        public NewsComment(long created, String authorName, String commentText) {
            this.created = created;
            this.creator_name = authorName;
            this.text = commentText;
        }

        public long getCreated() {
            return created;
        }

        public String getAuthorName() {
            return creator_name;
        }

        public String getCommentText() {
            return text;
        }
    }
}


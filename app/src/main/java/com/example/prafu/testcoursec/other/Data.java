package com.example.prafu.testcoursec.other;

/**
 * Created by Area51 on 13-Feb-17.
 */

public class Data {

    public String title;
    public String description;
    public String link;
    public String user;
    public String category;
    public String subject;

    public int getApproved() {
        return approved;
    }

    public void setApproved(int approved) {
        this.approved = approved;
    }

    public int approved;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Data(String title, String description, String link, String user, String category, String subject, int approved) {

        this.title = title;
        this.description = description;
        this.link = link;
        this.user = user;
        this.category = category;
        this.subject = subject;
        this.approved = approved;
    }

    public Data() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}

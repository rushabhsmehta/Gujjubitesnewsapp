package com.gujjubites;

public class newEvents {

    String description;
    String img_url;
    String tag;
    String title;
    String user;

    public newEvents() {

    }

    public newEvents(String description, String img_url, String tag, String title, String user)
    {
        this.title = title;
        this.description = description;
        this.img_url = img_url;
        this.tag = tag;
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}

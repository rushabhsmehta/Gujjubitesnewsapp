package com.gujjubites;

/**
 * Created by admin on 10/13/2017.
 */

public class BookMarks {
    int id;
    String key;

    public BookMarks(int id, String key) {
        this.id = id;
        this.key = key;
    }

    public BookMarks(String key) {
        this.key = key;
    }

    public BookMarks() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}

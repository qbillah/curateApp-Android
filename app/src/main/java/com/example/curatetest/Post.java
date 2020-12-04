package com.example.curatetest;

import java.util.ArrayList;

public class Post {

    private String URL;
    private ArrayList<String> Tags = new ArrayList<>();
    private String timestamp;
    private String uploadedBy;

    public Post(String U , String T , String Up , ArrayList<String> Tag){
        this.URL = U;
        this.timestamp = T;
        this.uploadedBy = Up;
        this.Tags = Tag;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public void setTags(ArrayList<String> tags) {
        Tags = tags;
    }

    public String getURL(){
        return URL;
    }

    public String getTimestamp(){
        return timestamp;
    }

    public String getUploadedBy(){
        return uploadedBy;
    }

    public ArrayList<String> getTags() {
        return Tags;
    }
}

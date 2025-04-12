package com.example.natksport;

public class Post {
    private String id;
    private String description;
    private String imageBase64;
    private String date;


    public Post() {
    }

    public Post(String id, String description, String imageBase64, String date) {
        this.id = id;
        this.description = description;
        this.imageBase64 = imageBase64;
        this.date = date;
    }


    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getImageBase64() {
        return imageBase64;
    }
    public String getDate() {return date;}
}

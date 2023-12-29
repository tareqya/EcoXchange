package com.example.ecoxchange.database;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class Post extends FirebaseKey{
    private String title;
    private String description;
    private String imagePath;
    private String imageUrl;
    private String userId;
    private String phone;
    private Date date;

    public Post() {
        date = new Date();
    }

    public String getTitle() {
        return title;
    }

    public Post setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Post setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Post setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    @Exclude
    public String getImageUrl() {
        return imageUrl;
    }

    public Post setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public Post setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Post setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Post setDate(Date date) {
        this.date = date;
        return this;
    }

    public Date getDate() {
        return date;
    }
}

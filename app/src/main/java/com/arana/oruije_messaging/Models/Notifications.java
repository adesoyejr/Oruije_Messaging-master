package com.arana.oruije_messaging.Models;

import android.graphics.Bitmap;

public class Notifications {
    String author, recipient, not_type, time_recieved, seen, username;
    Bitmap user_image;

    public Notifications(String author, String recipient, String not_type, String time_recieved, String seen, Bitmap user_image,String username){
        this.author = author;
        this.recipient = recipient;
        this.not_type = not_type;
        this.time_recieved = time_recieved;
        this.seen = seen;
        this.user_image = user_image;
        this.username = username;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNot_type(String not_type) {
        this.not_type = not_type;
    }

    public Bitmap getUser_image() {
        return user_image;
    }

    public void setUser_image(Bitmap user_image) {
        this.user_image = user_image;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public void setTime_recieved(String time_recieved) {
        this.time_recieved = time_recieved;
    }

    public String getNot_type() {
        return not_type;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSeen() {
        return seen;
    }

    public String getTime_recieved() {
        return time_recieved;
    }
}
package com.codose.betachat;

public class Chat {
    public String username;
    public String status;
    public String t_img;
    public String date;

    public Chat(){

    }

    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public Chat(String date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getT_img() {
        return t_img;
    }

    public void setT_img(String t_img) {
        this.t_img = t_img;
    }

    public Chat(String username, String status, String t_img) {
        this.username = username;
        this.status = status;
        this.t_img = t_img;
    }
}

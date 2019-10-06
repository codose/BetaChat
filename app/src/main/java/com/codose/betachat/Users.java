package com.codose.betachat;

public class Users {

    public String username;
    public String image;
    public String status;
    public String fullname;
    public String t_img;

    public String getT_img() {
        return t_img;
    }

    public void setT_img(String t_img) {
        this.t_img = t_img;
    }

    public Users(String username, String image, String status, String fullname, String t_img) {
        this.username = username;
        this.image = image;
        this.status = status;
        this.fullname = fullname;
        this.t_img = t_img;
    }

    public Users(String username, String image, String status, String fullname) {
        this.username = username;
        this.image = image;
        this.status = status;
        this.fullname = fullname;
    }

    public Users(String fullname) {
        this.fullname = fullname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Users(){

    }

    public Users(String username, String image, String status) {
        this.username = username;
        this.image = image;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

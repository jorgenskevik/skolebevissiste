package com.example.jorgenskevik.e_cardholders.models;

import android.media.session.MediaSession;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * The type User.
 */
public class User {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("first_name")
    @Expose
    private String first_name;

    @SerializedName("last_name")
    @Expose
    private String last_name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("picture_token")
    @Expose
    private String picture_token;

    @SerializedName("picture")
    @Expose
    private String picture;

    @SerializedName("user_role")
    @Expose
    private int user_role;

    @SerializedName("date_of_birth")
    @Expose
    private Date date_of_birth;

    @SerializedName("has_logged_in")
    @Expose
    private boolean has_logged_in;


    @SerializedName("has_set_picture")
    @Expose
    private boolean has_set_picture;

    @SerializedName("updated_at")
    @Expose
    private Date updated_at;

    @SerializedName("created_at")
    @Expose
    private Date created_at;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPicture_token() {
        return picture_token;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getUser_role() {
        return user_role;
    }

    public Date getDate_of_birth() {
        return date_of_birth;
    }

    public boolean isHas_set_picture() {
        return has_set_picture;
    }

    public void setHas_set_picture(boolean has_set_picture) {
        this.has_set_picture = has_set_picture;
    }

    public String getFullName(){
        return this.first_name + " " + this.last_name;
    }
}


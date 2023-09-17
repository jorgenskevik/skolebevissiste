package com.example.jorgenskevik.e_cardholders.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by jorgenskevik on 11.07.2018.
 */

public class UserDevice {

    @SerializedName("created_at")
    @Expose
    private Date created_at;

    @SerializedName("device_description")
    @Expose
    private String device_description;

    @SerializedName("device_token")
    @Expose
    private String device_token;

    @SerializedName("device_user")
    @Expose
    private String device_user;

    @SerializedName("firebase_project_id")
    @Expose
    private String firebase_project_id;

    @SerializedName("updated_at")
    @Expose
    private Date updated_at;

    public UserDevice(String device_token){
        this.device_token = device_token;
    }

}

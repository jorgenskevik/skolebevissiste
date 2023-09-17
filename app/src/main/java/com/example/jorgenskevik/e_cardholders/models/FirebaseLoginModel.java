package com.example.jorgenskevik.e_cardholders.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by jorgenskevik on 11.07.2018.
 */

public class FirebaseLoginModel {


    @SerializedName("phone")
    @Expose
    public String phone;
    /*** The User.*/
    @SerializedName("firebase_token")
    @Expose
    public String firebase_token;

    public FirebaseLoginModel(String phone, String firebase_token){
        this.phone = phone;
        this.firebase_token = firebase_token;
    }
}

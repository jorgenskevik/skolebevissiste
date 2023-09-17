package com.example.jorgenskevik.e_cardholders.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by jorgenskevik on 11.07.2018.
 */

public class UnitMembership {

    @SerializedName("created_at")
    @Expose
    private Date created_at;

    @SerializedName("expiration_date")
    @Expose
    private Date expiration_date;

    @SerializedName("student_class")
    @Expose
    private String student_class;

    @SerializedName("student_number")
    @Expose
    private String student_number;

    @SerializedName("updated_at")
    @Expose
    private Date updated_at;

    @SerializedName("member")
    @Expose
    private String member;

    @SerializedName("member_unit")
    @Expose
    private int member_unit;

    @SerializedName("id")
    @Expose
    private int id;

    public Date getExpiration_date() {
        return expiration_date;
    }

    public String getStudent_class() {
        return student_class;
    }

    public String getStudent_number() {
        return student_number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

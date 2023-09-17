package com.example.jorgenskevik.e_cardholders.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by jorgenskevik on 11.07.2018.
 */

public class Unit {

    @SerializedName("createdAt")
    @Expose
    private Date createdAt;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("organisation_number")
    @Expose
    private String organisation_number;

    @SerializedName("private_contact_email")
    @Expose
    private String private_contact_email;

    @SerializedName("private_contact_name")
    @Expose
    private String private_contact_name;

    @SerializedName("public_contact_email")
    @Expose
    private String public_contact_email;

    @SerializedName("public_contact_phone")
    @Expose
    private String public_contact_phone;

    @SerializedName("short_name")
    @Expose
    private String short_name;

    @SerializedName("unit_logo")
    @Expose
    private String unit_logo;

    @SerializedName("updated_at")
    @Expose
    private Date updated_at;

    @SerializedName("created_at")
    @Expose
    private Date created_at;

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("card_type")
    @Expose
    private String card_type;

    @SerializedName("authentication_type")
    @Expose
    private String authentication_type;

    @SerializedName("small_unit_logo")
    @Expose
    private String small_unit_logo;

    public Unit(String unit_name, int id, String short_name){
        this.name = unit_name;
        this.id = id;
        this.short_name = short_name;
    }

    public String getSmall_unit_logo() {
        return small_unit_logo;
    }


    public String getCard_type() {
        return card_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublic_contact_email() {
        return public_contact_email;
    }

    public String getPublic_contact_phone() {
        return public_contact_phone;
    }

    public String getShort_name() {
        return short_name;
    }

    public String getUnit_logo() {
        return unit_logo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

package com.example.jorgenskevik.e_cardholders.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The type Login model.
 */
public class LoginModel {
    /**
     * The Token.
     */
    @SerializedName("user")
    @Expose
    private User user;

    /**
     * The User.
     */
    @SerializedName("unit")
    @Expose
    private Unit unit;

    @SerializedName("unit_membership")
    @Expose
    private UnitMembership unitMembership;

    @SerializedName("auth_token")
    @Expose
    private String auth_token;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public UnitMembership getUnitMembership() {
        return unitMembership;
    }

    public String getAuth_token() {
        return auth_token;
    }

}

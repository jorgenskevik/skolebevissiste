package com.example.jorgenskevik.e_cardholders.Variables;

import android.content.SharedPreferences;

/**
 * Created by jorgenskevik on 23.03.2017.
 */
public class KVTVariables {

    private static final String BASE_URL = "https://api-skolebevis.kortfri.no/api/v1/";
    private static String picture = "";

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getPicture() {
        return picture;
    }

    public static void setPicture(String picture) {
        KVTVariables.picture = picture;
    }

}

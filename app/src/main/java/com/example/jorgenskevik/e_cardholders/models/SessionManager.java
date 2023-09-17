package com.example.jorgenskevik.e_cardholders.models;

/**
 * Created by jorgenskevik on 27.03.2017.
 */

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * The type Session manager.
 */
public class SessionManager {
    /**
     * The Pref.
     */
// Shared Preferences
    private SharedPreferences pref;

    /**
     * The Editor.
     */
// Editor for Shared preferences
    private Editor editor;

    /**
     * The Context.
     */
// Context
    private Context _context;

    /**
     * The Private mode.
     */
// Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_FULL_NAME = "full_name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ID = "id";
    public static final String KEY_ROLE = "role";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_PICTURETOKEN = "pictureToken";
    public static final String KEY_EXPERATIONDATE = "experationDate";
    public static final String KEY_STUDENTNUMBER = "studentNumber";
    public static final String KEY_PATH = "path";
    public static final String KEY_BIRTHDATE = "birthDate";
    public static final String KEY_PICTURE = "picture";
    public static final String KEY_CHECK = "check";
    public static final String KEY_UNIT_MEMBERSHIP_ID = "unit_membership_id";
    public static final String KEY_STUDENT_CLASS = "student_class";
    public static final String KEY_UNIT_NAME = "unit_name";
    public static final String KEY_UNIT_SHORT_NAME = "unit_short_name";
    public static final String KEY_PUBLIC_CONTACT_PHONE = "public_contact_phone";
    public static final String KEY_PUBLIC_CONTACT_EMAIL = "public_contact_email";
    public static final String KEY_UNIT_LOGO = "unit_logo";
    public static final String KEY_UNIT_LOGO_SHORT = "unit_logo_short";
    public static final String KEY_UNIT_ID = "unit_id";
    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_CARD_TYPE = "card_type";
    public static final String KEY_MEDIA_PATH = "media_path";
    public static final String KEY_HAS_SET_PICTURE = "has_set_picture";


    /**
     * Instantiates a new Session manager.
     *
     * @param context the context
     */
// Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }




    public void create_login_session_unit(String unit_name, String unit_short_name, String unit_logo, String unit_logo_short, int unit_id,
                                         String public_email, String public_phone, String card_type){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_UNIT_NAME, unit_name);
        editor.putString(KEY_UNIT_SHORT_NAME, unit_short_name);
        editor.putString(KEY_UNIT_LOGO, unit_logo);
        editor.putString(KEY_UNIT_LOGO_SHORT, unit_logo_short);
        editor.putInt(KEY_UNIT_ID, unit_id);
        editor.putString(KEY_PUBLIC_CONTACT_EMAIL, public_email);
        editor.putString(KEY_PUBLIC_CONTACT_PHONE, public_phone);
        editor.putString(KEY_CARD_TYPE, card_type);
        editor.commit();
    }


    public void create_login_session_unit_member(String expiration_date, String student_class, String student_number, int unit_membership_id ){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_EXPERATIONDATE, expiration_date);
        editor.putString(KEY_STUDENT_CLASS, student_class);
        editor.putString(KEY_STUDENTNUMBER, student_number);
        editor.putInt(KEY_UNIT_MEMBERSHIP_ID, unit_membership_id);
        editor.commit();
    }

    public void create_login_session_user(String full_name, String user_email, String token, String user_id, int role,
                                          String pictureToken, String birthday, String picture, boolean has_set_picture){

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_FULL_NAME, full_name);
        editor.putString(KEY_EMAIL, user_email);
        editor.putString(KEY_ID, user_id);
        editor.putString(KEY_TOKEN, token);
        editor.putInt(KEY_ROLE, role);
        editor.putString(KEY_PICTURETOKEN, pictureToken);
        editor.putString(KEY_BIRTHDATE, birthday);
        editor.putString(KEY_PICTURE, picture);
        editor.putBoolean(KEY_HAS_SET_PICTURE, has_set_picture);
        // commit changes
        editor.commit();
    }

    public void update_user(String name, String email, String id, int role, String pictureToken, String dateOfBirth
            , String picture, boolean has_set_picture){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_FULL_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // Storing id in pref
        editor.putString(KEY_ID, id);

        // Storing role in pref
        editor.putInt(KEY_ROLE, role);

        // Storing role in pref
        editor.putString(KEY_PICTURETOKEN, pictureToken);

        // Storing dateofbirth in pref
        editor.putString(KEY_BIRTHDATE, dateOfBirth);

        // Storing role in pref

        editor.putString(KEY_PICTURE, picture);

        editor.putBoolean(KEY_HAS_SET_PICTURE, has_set_picture);

        // commit changes
        editor.commit();
    }

    /**
     * Update picture token.
     *
     * @param pictureToken the picture token
     */
    public void updatePictureToken(String pictureToken){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing role in pref
        editor.putString(KEY_PICTURETOKEN, pictureToken);

        // commit changes
        editor.apply();
    }


    public void update_boolean(Boolean path){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing path in pref
        editor.putBoolean(KEY_HAS_SET_PICTURE, path);

        // Storing picture in pref
        //editor.putString(KEY_PICTURE, path);

        // commit changes
        editor.apply();
    }
    /**
     * Update path.
     *
     * @param path the path
     */
    public void updatePath(String path){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing path in pref
        editor.putString(KEY_PATH, path);

        editor.apply();
    }


    public void setMedia_path(String media_path){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing role in pref
        editor.putString(KEY_MEDIA_PATH, media_path);

        // commit changes
        editor.apply();

    }


    public void updatePicture(String picture){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing role in pref
        editor.putString(KEY_PICTURE, picture);

        // commit changes
        editor.apply();

    }

    public HashMap<String, String> getMedia_path(){
        HashMap<String, String> unitMember = new HashMap<String, String>();
        unitMember.put(KEY_MEDIA_PATH, pref.getString(KEY_MEDIA_PATH, null));
        return unitMember;
    }

    /**
     * Get user details hash map.
     *
     * @return the hash map
     */


    public HashMap<String, String> getUnitMemberDetails(){
        HashMap<String, String> unitMember = new HashMap<String, String>();
        unitMember.put(KEY_EXPERATIONDATE, pref.getString(KEY_EXPERATIONDATE, null));
        unitMember.put(KEY_STUDENT_CLASS, pref.getString(KEY_STUDENT_CLASS, null));
        unitMember.put(KEY_STUDENTNUMBER, pref.getString(KEY_STUDENTNUMBER, null));
        unitMember.put(KEY_UNIT_MEMBERSHIP_ID, String.valueOf(pref.getInt(KEY_UNIT_MEMBERSHIP_ID, 0)));

        return unitMember;
    }


    public HashMap<String, String> getUnitDetails(){
        HashMap<String, String> unit = new HashMap<String, String>();
        unit.put(KEY_UNIT_NAME, pref.getString(KEY_UNIT_NAME, null));
        unit.put(KEY_UNIT_SHORT_NAME, pref.getString(KEY_UNIT_SHORT_NAME, null));
        unit.put(KEY_UNIT_LOGO_SHORT, pref.getString(KEY_UNIT_LOGO_SHORT, null));
        unit.put(KEY_UNIT_LOGO, pref.getString(KEY_UNIT_LOGO, null));
        unit.put(KEY_UNIT_ID, String.valueOf(pref.getInt(KEY_UNIT_ID, 0)));
        unit.put(KEY_PUBLIC_CONTACT_EMAIL, pref.getString(KEY_PUBLIC_CONTACT_EMAIL, null));
        unit.put(KEY_PUBLIC_CONTACT_PHONE, pref.getString(KEY_PUBLIC_CONTACT_PHONE, null));
        unit.put(KEY_CARD_TYPE, pref.getString(KEY_CARD_TYPE, null));

        return unit;
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name

        user.put(KEY_FULL_NAME, pref.getString(KEY_FULL_NAME, null));

        // user email
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // user id
        user.put(KEY_ID, pref.getString(KEY_ID, null));

        // user token
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));

        // user studentNumber
        user.put(KEY_STUDENTNUMBER, pref.getString(KEY_STUDENTNUMBER, null));

        // user role
        user.put(KEY_ROLE, String.valueOf(pref.getInt(KEY_ROLE, 0)));

        user.put(KEY_UNIT_ID, String.valueOf(pref.getInt(KEY_UNIT_ID, 0)));

        // user expertaionDat
        user.put(KEY_EXPERATIONDATE, pref.getString(KEY_EXPERATIONDATE, null));

        // user pictureToken
        user.put(KEY_PICTURETOKEN, pref.getString(KEY_PICTURETOKEN, null));

        // user path
        user.put(KEY_PATH, pref.getString(KEY_PATH, null));

        // user birthDate
        user.put(KEY_BIRTHDATE, pref.getString(KEY_BIRTHDATE, null));

        // user picture
        user.put(KEY_PICTURE, pref.getString(KEY_PICTURE, null));

        // user check
        user.put(KEY_CHECK, pref.getString(KEY_CHECK, null));

        user.put(KEY_UNIT_SHORT_NAME, pref.getString(KEY_UNIT_SHORT_NAME, null));

        user.put(KEY_UNIT_LOGO, pref.getString(KEY_UNIT_LOGO, null));

        user.put(KEY_PHONE_NUMBER, pref.getString(KEY_PHONE_NUMBER, null));

        user.put(KEY_HAS_SET_PICTURE, String.valueOf(pref.getBoolean(KEY_HAS_SET_PICTURE, false)));

        // return user
        return user;
    }

    /**
     * Logout user.
     */


    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.remove(KEY_UNIT_NAME);
        editor.remove(KEY_UNIT_LOGO);
        editor.remove(KEY_UNIT_LOGO_SHORT);
        editor.remove(KEY_UNIT_ID);
        editor.remove(KEY_PUBLIC_CONTACT_EMAIL);
        editor.remove(KEY_PUBLIC_CONTACT_PHONE);
        editor.remove(KEY_STUDENT_CLASS);
        editor.remove(KEY_UNIT_MEMBERSHIP_ID);
        editor.remove(IS_LOGIN);
        editor.remove(KEY_FULL_NAME);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_ID);
        editor.remove(KEY_ROLE);
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_PICTURETOKEN);
        editor.remove(KEY_EXPERATIONDATE);
        editor.remove(KEY_STUDENTNUMBER);
        editor.remove(KEY_BIRTHDATE);
        editor.remove(KEY_PATH);
        editor.remove(KEY_UNIT_NAME);
        editor.remove(KEY_STUDENT_CLASS);
        editor.remove(KEY_UNIT_SHORT_NAME);
        editor.remove(KEY_MEDIA_PATH);
        editor.remove(KEY_CARD_TYPE);
        editor.remove(KEY_PICTURE);
        editor.remove(KEY_CHECK);
        editor.remove(KEY_UNIT_SHORT_NAME);
        editor.remove(KEY_PHONE_NUMBER);
        editor.remove(KEY_CARD_TYPE);
        editor.remove(KEY_HAS_SET_PICTURE);
        editor.commit();
    }
}


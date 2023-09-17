package com.example.jorgenskevik.e_cardholders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.jorgenskevik.e_cardholders.Variables.KVTVariables;
import com.example.jorgenskevik.e_cardholders.models.Login_model;
import com.example.jorgenskevik.e_cardholders.models.SessionManager;
import com.example.jorgenskevik.e_cardholders.models.Unit;
import com.example.jorgenskevik.e_cardholders.models.UnitMembership;
import com.example.jorgenskevik.e_cardholders.models.User;
import com.example.jorgenskevik.e_cardholders.remote.UserAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MemberActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    SessionManager sessionManager;
    ImageView short_logo_view;
    TextView firstAndSirName,
            card,
            memberNumber,
    short_school_name,
    BirthDay;
    String firstAndSirNameString,
    thisExpDate,
            unitname,
    birthdayString,
    expirationDateString,
    formattedDate,
    authenticateString;
    SimpleDateFormat simpleDateFormat;
    Date startDate,
    date;
    DateFormat targetFormat;
    Intent intent;
    HashMap<String, String> userDetails, unit_details, unit_membership_details;
    RelativeLayout r1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.member_view);

        sessionManager = new SessionManager(getApplicationContext());

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);

        //View barcode

        short_logo_view = (ImageView) findViewById(R.id.sircle);
        card = (TextView) findViewById(R.id.skolebevis);
        firstAndSirName = (TextView) findViewById(R.id.textView11);
        short_school_name = (TextView) findViewById(R.id.textView16);
        memberNumber = (TextView) findViewById(R.id.textView17);

        BirthDay = (TextView) findViewById(R.id.textView2);
        userDetails = sessionManager.getUserDetails();
        unit_details = sessionManager.getUnitDetails();
        unit_membership_details = sessionManager.getUnitMemberDetails();

        String small_logo = unit_details.get(SessionManager.KEY_UNIT_LOGO);

        unitname = unit_details.get(SessionManager.KEY_UNIT_NAME);
        firstAndSirNameString = userDetails.get(SessionManager.KEY_FULL_NAME);
        birthdayString = userDetails.get(SessionManager.KEY_BIRTHDATE);
        targetFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.GERMANY);


        //JodaTimeAndroid.init(this);

        r1 = (RelativeLayout) findViewById(R.id.background);
        card.setText(getResources().getString(R.string.member_sertificat));

        Glide.with(this).load(small_logo).into(short_logo_view);


        firstAndSirName.setText(firstAndSirNameString);
        short_school_name.setText(unitname);
        memberNumber.setText(userDetails.get(SessionManager.KEY_STUDENTNUMBER));

        sessionManager = new SessionManager(getApplicationContext());
        expirationDateString = unit_membership_details.get(SessionManager.KEY_EXPERATIONDATE);

        startDate = null;
        try {
            assert expirationDateString != null;
            startDate = simpleDateFormat.parse(expirationDateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (System.currentTimeMillis() > startDate.getTime()) {
            //Ugyldig
            r1.setBackgroundColor(ContextCompat.getColor(this, R.color.invalid_backgroud));
        } else {
            //gyldig
            r1.setBackgroundColor(ContextCompat.getColor(this, R.color.valid_backgroud));

            targetFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.GERMANY);
            try {
                if(Calendar.getInstance().get(Calendar.MONTH) + 1 < 8){
                    r1.setBackgroundColor(ContextCompat.getColor(this, R.color.valid_backgroud));
                    BirthDay.setText(getResources().getString(R.string.spring) + " " + Calendar.getInstance().get(Calendar.YEAR));

                }else{
                    r1.setBackgroundColor(ContextCompat.getColor(this, R.color.valid_backgroud));
                    thisExpDate = unit_membership_details.get(SessionManager.KEY_EXPERATIONDATE);
                    assert thisExpDate != null;
                    date = simpleDateFormat.parse(thisExpDate);
                    assert date != null;
                    formattedDate = targetFormat.format(date);
                    BirthDay.setText(getResources().getString(R.string.fall) + " " + Calendar.getInstance().get(Calendar.YEAR));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        ImageView btn = (ImageView) findViewById(R.id.imageView2);
        btn.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MemberActivity.this, v);
            popup.setOnMenuItemClickListener(MemberActivity.this);
            popup.inflate(R.menu.popup_menu_blues);
            popup.show();
        });
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Toast.makeText(this, "Selected Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        int id = item.getItemId();
        if (id == R.id.loggOut){
            sessionManager.logoutUser();
            intent = new Intent(MemberActivity.this, LandingPage.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.updateProfile){
            updateuser();
            return true;
        }
        else if (id == R.id.policy){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.kortfri.no/privacypolicy.html"));
            startActivity(browserIntent);
            return true;
        }
        else if (id == R.id.kontakt){
            Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.trondheimbluesklubb.no/kontakt-oss"));
            startActivity(browserIntent2);
            return true;
        }
        else if (id == R.id.konsert){
            Intent browserIntent3 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.trondheimbluesklubb.no"));
            startActivity(browserIntent3);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void updateuser(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(KVTVariables.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        userDetails = sessionManager.getUserDetails();

        authenticateString = "Bearer " + userDetails.get(SessionManager.KEY_TOKEN);

        UserAPI userapi = retrofit.create(UserAPI.class);
        int unit_id = Integer.parseInt(Objects.requireNonNull(unit_details.get(SessionManager.KEY_UNIT_ID)));
        String super_token = "token " + userDetails.get(SessionManager.KEY_TOKEN);
        userapi.getUser(super_token, String.valueOf(unit_id)).enqueue(new Callback<Login_model>() {
            @Override
            public void onResponse(@NonNull Call<Login_model> call, @NonNull Response<Login_model> response) {
                if (response.isSuccessful()) {
                    Login_model login_model = response.body();
                    assert login_model != null;
                    User user = login_model.getUser();
                    Unit unit  = login_model.getUnit();
                    UnitMembership unitMembership = login_model.getUnitMembership();

                    String full_name = user.getFullName();
                    String emailString = user.getEmail();
                    String picture = user.getPicture();

                    String user_id = user.getId();
                    int role = user.getUser_role();
                    String pictureToken = user.getPicture_token();

                    int unitMembershipId = unitMembership.getId();
                    String student_class = unitMembership.getStudent_class();
                    String student_number = unitMembership.getStudent_number();

                    String card_type = unit.getCard_type();
                    String unit_name = unit.getName();
                    String unit_short_name = unit.getShort_name();
                    String public_contact_phone = unit.getPublic_contact_phone();
                    String public_contact_email = unit.getPublic_contact_email();
                    String unit_logo = unit.getUnit_logo();
                    String unit_logo_short = unit.getSmall_unit_logo();
                    boolean has_set_picture = user.isHas_set_picture();
                    int unit_id = unit.getId();

                    Date dateToExpiration = unitMembership.getExpiration_date();
                    Date birthdayDate = user.getDate_of_birth();

                    DateTime timeToExpiration = new DateTime(dateToExpiration);
                    DateTime timeBirthday = new DateTime(birthdayDate);

                    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd-MMM-yyyy");
                    DateTimeFormatter dateTimeFormatter2 = DateTimeFormat.forPattern("yyyy-MM-dd");

                    String birthDateString = dateTimeFormatter.print(timeBirthday);
                    String expirationString = dateTimeFormatter2.print(timeToExpiration);

                    sessionManager.update_user(full_name, emailString, user_id, role, pictureToken, birthDateString, picture, has_set_picture);

                    sessionManager.create_login_session_unit(unit_name, unit_short_name, unit_logo, unit_logo_short, unit_id,
                            public_contact_email, public_contact_phone, card_type);

                    sessionManager.create_login_session_unit_member(expirationString, student_class, student_number, unitMembershipId);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.userUpdated), Toast.LENGTH_SHORT).show();

                    userDetails = sessionManager.getUserDetails();
                    unit_details = sessionManager.getUnitDetails();

                    card.setText(getResources().getString(R.string.member_sertificat));
                    firstAndSirName.setText(firstAndSirNameString);
                    short_school_name.setText(unitname);
                    memberNumber.setText(userDetails.get(SessionManager.KEY_STUDENTNUMBER));


                    Glide.with(getApplicationContext()).load(unit_details.get(SessionManager.KEY_UNIT_LOGO)).into(short_logo_view);

                    startDate = null;
                    try {
                        startDate = simpleDateFormat.parse(expirationString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (System.currentTimeMillis() > startDate.getTime()) {
                        //Ugyldig
                        r1.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.invalid_backgroud));
                    } else {
                        //gyldig
                        r1.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.valid_backgroud));

                        targetFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.GERMANY);
                        try {
                            if(Calendar.getInstance().get(Calendar.MONTH) + 1 < 8){
                                r1.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.valid_backgroud));
                                BirthDay.setText(getResources().getString(R.string.spring) + " " + Calendar.getInstance().get(Calendar.YEAR));

                            }else{
                                r1.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.valid_backgroud));
                                thisExpDate = unit_membership_details.get(SessionManager.KEY_EXPERATIONDATE);
                                assert thisExpDate != null;
                                date = simpleDateFormat.parse(thisExpDate);
                                assert date != null;
                                formattedDate = targetFormat.format(date);
                                BirthDay.setText(getResources().getString(R.string.fall) + " " + Calendar.getInstance().get(Calendar.YEAR));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    finish();
                    startActivity(getIntent());

                }else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.userNotUpdated), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Login_model> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.userNotUpdated), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
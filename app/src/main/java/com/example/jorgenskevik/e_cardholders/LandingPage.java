package com.example.jorgenskevik.e_cardholders;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.example.jorgenskevik.e_cardholders.models.SessionManager;
import java.util.HashMap;

/**
 * Created by jorgenskevik on 29.12.2017.
 */

public class LandingPage extends Activity{
    TextView open_href;
    TextView open_schools;
    SessionManager sessionManager;
    TextView kortfri;
    Uri uri_href;
    public static final int CAM_REQUEST_CODE = 4545;
    public static final int maxBuildVersion = 6;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.landingpage);
        open_href = (TextView) findViewById(R.id.openHref);
        open_schools = (TextView) findViewById(R.id.terms_and_conditions);
        kortfri = (TextView) findViewById(R.id.kortfrilogo);

        kortfri.setText("KortFri", TextView.BufferType.SPANNABLE);
        Spannable span = (Spannable) kortfri.getText();
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.logogreycolor)), 0, "Kort".length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        HashMap<String, String> unit = sessionManager.getUnitDetails();

        String name = user.get(SessionManager.KEY_FULL_NAME);
        String id = user.get(SessionManager.KEY_ID);
        String email = user.get(SessionManager.KEY_EMAIL);
        String token = user.get(SessionManager.KEY_TOKEN);
        String card = unit.get(SessionManager.KEY_CARD_TYPE);

        if (name == null || id == null || email == null || token == null) {

            open_href.setOnClickListener(v -> {
                uri_href = Uri.parse("https://www.kortfri.no/privacypolicy.html");
                Intent terms_page = new Intent(Intent.ACTION_VIEW, uri_href);
                startActivity(terms_page);
            });
        }else {
            assert card != null;
            Intent intent;
            if (card.equals("membership_card")){
                intent = new Intent(LandingPage.this, MemberActivity.class);
            }else{
                intent = new Intent(LandingPage.this, UserActivity.class);
            }
            startActivity(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void open_schools(View view) {
        String buildVersion = Build.VERSION.RELEASE;
        String firstLetter = String.valueOf(buildVersion.charAt(0));
        int number = Integer.parseInt(firstLetter);

        if(number < maxBuildVersion){
            Intent school_page = new Intent(LandingPage.this, MainActivity.class);
            startActivity(school_page);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent school_page = new Intent(LandingPage.this, MainActivity.class);
            startActivity(school_page);
        }else{
            String[] permissionRequest = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissionRequest, CAM_REQUEST_CODE);
        }
    }
}

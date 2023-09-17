package com.example.jorgenskevik.e_cardholders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import java.util.HashMap;
import android.widget.Toast;
import com.example.jorgenskevik.e_cardholders.models.SessionManager;

/**
 * The type Bar code activity.
 */
public class BarCodeActivity extends Activity{
    TextView button_back;
    TextView continue_picture;
    EditText codeString;
    HashMap<String, String> userDetails;
    SessionManager sessionManager;
    String fourDigits;
    Context context;
    int duration;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.picture_info);

        button_back = (TextView) findViewById(R.id.button_back);
        continue_picture = (TextView) findViewById(R.id.button_ok);
        codeString = (EditText) findViewById(R.id.code_picture);

        sessionManager = new SessionManager(getApplicationContext());
        userDetails = sessionManager.getUserDetails();
        fourDigits = userDetails.get(SessionManager.KEY_PICTURETOKEN);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);



        button_back.setOnClickListener(v -> {
            Intent back = new Intent(BarCodeActivity.this, UserActivity.class);
            startActivity(back);
        });

    }
    public void open_picture_view(View v){
        if (fourDigits.trim().equals(codeString.getText().toString())){
            Intent back = new Intent(BarCodeActivity.this, Picture_info.class);
            startActivity(back);
        }else {
            context = getApplicationContext();
            duration = Toast.LENGTH_SHORT;
            toast = Toast.makeText(context, R.string.wrongCode, duration);
            toast.show();
        }
    }
}



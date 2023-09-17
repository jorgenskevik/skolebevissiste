package com.example.jorgenskevik.e_cardholders;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.jorgenskevik.e_cardholders.Variables.KVTVariables;
import com.example.jorgenskevik.e_cardholders.models.FirebaseLoginModel;
import com.example.jorgenskevik.e_cardholders.models.LoginModel;
import com.example.jorgenskevik.e_cardholders.models.SessionManager;
import com.example.jorgenskevik.e_cardholders.models.Token;
import com.example.jorgenskevik.e_cardholders.models.Unit;
import com.example.jorgenskevik.e_cardholders.models.UnitMembership;
import com.example.jorgenskevik.e_cardholders.models.User;
import com.example.jorgenskevik.e_cardholders.models.UserDevice;
import com.example.jorgenskevik.e_cardholders.remote.UserAPI;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.danlew.android.joda.JodaTimeAndroid;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity  implements View.OnClickListener {

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    private ViewGroup mPhoneNumberViews;
    private ViewGroup mSignedInViews;

    private TextView mStatusText;
    private TextView mDetailText;

    private EditText mPhoneNumberField;
    private EditText mVerificationField;
    private EditText landskode;

    private Button mStartButton;
    private Button mVerifyButton;
    private Button mResendButton;

    ProgressBar progressBar;
    SessionManager sessionManager;

    private int unit_id;

    private String mVerificationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_login_page);

        if (mVerificationId == null && savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        Intent mIntent = getIntent();
        unit_id = mIntent.getIntExtra("unit_id_i_need", 0);

        JodaTimeAndroid.init(this);
        sessionManager = new SessionManager(getApplicationContext());

        progressBar = findViewById(R.id.progressBar);

        // Assign views
        mPhoneNumberViews = findViewById(R.id.phone_auth_fields);
        mSignedInViews = findViewById(R.id.signed_in_buttons);

        mStatusText = findViewById(R.id.status);
        mDetailText = findViewById(R.id.detail);

        mPhoneNumberField = findViewById(R.id.field_phone_number);
        mVerificationField = findViewById(R.id.field_verification_code);
        landskode = findViewById(R.id.picker);

        mStartButton = findViewById(R.id.button_start_verification);
        mVerifyButton = findViewById(R.id.button_verify_phone);
        mResendButton = findViewById(R.id.button_resend);
        Button mSignOutButton = findViewById(R.id.sign_out_button);

        // Assign click listeners
        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);

        mStartButton.setTextColor(ContextCompat.getColor(this, R.color.logobluecolor));

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;
                updateUI(credential);

                signInWithCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                // [START_EXCLUDE silent]
                Log.w(TAG, "onVerificationFailed", e);
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    mPhoneNumberField.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                updateUI(STATE_CODE_SENT);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(mVerificationInProgress && validatePhoneNumber()){
            sendVerificationCode(landskode.getText().toString() + mPhoneNumberField.getText().toString());
        }
    }
    // [END on_start_check_user]

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
        outState.putString(KEY_VERIFY_IN_PROGRESS, mVerificationId);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
        mVerificationId = savedInstanceState.getString(KEY_VERIFY_IN_PROGRESS);
    }

    private void sendVerificationCode(String number) {

        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)		 // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)				 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)		 // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        mVerificationInProgress = true;
        mStatusText.setVisibility(View.INVISIBLE);
    }


    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        if(!phoneNumber.equals("")){
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(phoneNumber)		 // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)				 // Activity (for callback binding)
                            .setCallbacks(mCallbacks)		 // OnVerificationStateChangedCallbacks
                            .setForceResendingToken(token)
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }else{
            Toast.makeText(this, R.string.Skrivinn, Toast.LENGTH_LONG).show();
        }
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
        // [END verify_with_code]
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();

                        updateUI(user);
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            mVerificationField.setError("Invalid code.");
                        }
                        updateUI(STATE_SIGNIN_FAILED);
                    }
                });
    }


    // [START sign_in_with_phone]


    private void signOut() {
        mAuth.signOut();
        updateUI(STATE_INITIALIZED);
    }

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }


    private void updateUI(FirebaseUser user) {
        updateUI(LoginActivity.STATE_SIGNIN_SUCCESS, user, null);
    }

    private void updateUI(PhoneAuthCredential cred) {
        updateUI(LoginActivity.STATE_VERIFY_SUCCESS, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button
                enableViews(mStartButton, mPhoneNumberField);
                disableViews(mVerifyButton, mResendButton, mVerificationField);
                mDetailText.setText(null);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                enableViews(mVerifyButton, mResendButton, mPhoneNumberField, mVerificationField);
                disableViews(mStartButton);
                mDetailText.setText(R.string.status_code_sent);
                mDetailText.setTextColor(Color.parseColor("#43a047"));
                mVerifyButton.setTextColor(ContextCompat.getColor(this, R.color.logobluecolor));
                mStartButton.setTextColor(ContextCompat.getColor(this, R.color.logogreycolor));
                mResendButton.setTextColor(ContextCompat.getColor(this, R.color.logobluecolor));

                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                enableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                        mVerificationField);
                mDetailText.setText(R.string.status_verification_failed);
                mDetailText.setTextColor(Color.parseColor("#dd2c00"));
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                disableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                        mVerificationField);
                mDetailText.setText(R.string.status_verification_succeeded);
                mDetailText.setTextColor(Color.parseColor("#43a047"));
                progressBar.setVisibility(View.INVISIBLE);

                // Set the verification text based on the credential
                if (cred != null) {

                    if (cred.getSmsCode() != null) {
                        mVerificationField.setText(cred.getSmsCode());
                    } else {
                        mVerificationField.setText(R.string.instant_validation);
                        mVerificationField.setTextColor(Color.parseColor("#4bacb8"));
                    }
                }

                break;
            case STATE_SIGNIN_FAILED:

                // No-op, handled by sign-in check
                mDetailText.setText(R.string.status_sign_in_failed);
                mDetailText.setTextColor(Color.parseColor("#dd2c00"));
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case STATE_SIGNIN_SUCCESS:

                // Np-op, handled by sign-in check
                mStatusText.setText(R.string.signed_in);
                break;
        }

        if (user == null) {
            // Signed out
            mPhoneNumberViews.setVisibility(View.VISIBLE);
            mSignedInViews.setVisibility(View.GONE);

            //mStatusText.setText(R.string.sign_out);;
        } else {

            // Signed in
            mPhoneNumberViews.setVisibility(View.GONE);


            FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
            assert mUser != null;
            mUser.getIdToken(true)
                    .addOnCompleteListener(task -> {

                        String idToken = Objects.requireNonNull(task.getResult()).getToken();
                        Gson gson = new GsonBuilder()
                                .setLenient()
                                .create();

                        String fcm_token = mAuth.getUid();

                        //local eller base
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(KVTVariables.getBaseUrl())
                                .addConverterFactory(GsonConverterFactory.create(gson))
                                .build();

                        UserAPI userapi = retrofit.create(UserAPI.class);
                        Intent mIntent = getIntent();
                        int intValue = mIntent.getIntExtra("Unit_ID", unit_id);

                        final FirebaseLoginModel firebaseLoginModel = new FirebaseLoginModel(mPhoneNumberField.getText().toString(), idToken);

                        userapi.userLogin(firebaseLoginModel, String.valueOf(intValue)).enqueue(new Callback<LoginModel>() {

                            private void storeInSession(SessionManager sessionManager, User user1, String token, Unit unit, UnitMembership unitMembership){
                                String full_name = user1.getFullName();
                                String emailString = user1.getEmail();
                                String picture = user1.getPicture();
                                String user_id = user1.getId();
                                int role = user1.getUser_role();
                                String pictureToken = user1.getPicture_token();

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
                                int unit_id = unit.getId();

                                java.util.Date dateToExpiration = unitMembership.getExpiration_date();
                                java.util.Date birthdayDate = user1.getDate_of_birth();

                                DateTime timeToExpiration = new DateTime(dateToExpiration);
                                DateTime timeBirthday = new DateTime(birthdayDate);

                                DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd-MMM-yyyy");
                                DateTimeFormatter dateTimeFormatter2 = DateTimeFormat.forPattern("yyyy-MM-dd");

                                String birthDateString = dateTimeFormatter.print(timeBirthday);
                                String expirationString = dateTimeFormatter2.print(timeToExpiration);

                                sessionManager.create_login_session_user(full_name, emailString,
                                        token, user_id, role, pictureToken, birthDateString, picture, user1.isHas_set_picture());

                                sessionManager.create_login_session_unit(unit_name, unit_short_name, unit_logo, unit_logo_short, unit_id,
                                        public_contact_email, public_contact_phone, card_type);

                                sessionManager.create_login_session_unit_member(expirationString, student_class, student_number, unitMembershipId);

                            }
                            @Override
                            public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                                if (!response.isSuccessful()) {
                                    Context context = getApplicationContext();
                                    CharSequence text = response.toString();
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                    return;
                                }

                                LoginModel LoginList = response.body();

                                sessionManager = new SessionManager(getApplicationContext());

                                assert LoginList != null;
                                User user1 = LoginList.getUser();
                                String token = LoginList.getAuth_token();
                                Unit unit  = LoginList.getUnit();
                                UnitMembership unit_membership = LoginList.getUnitMembership();
                                String bearToken = "token " + token;

                                storeInSession(sessionManager, user1, token, unit, unit_membership);
                                sendRegistrationToServer(fcm_token, bearToken);

                                if (LoginList.getUser().getUser_role() == 1 || LoginList.getUser().getUser_role() == 2) {
                                    Context context = getApplicationContext();
                                    int duration = Toast.LENGTH_LONG;
                                    Toast toast = Toast.makeText(context, R.string.youareadmin, duration);
                                    toast.show();
                                    return;
                                } else {
                                    if (!LoginList.getUser().getFirst_name().trim().equals("") && !LoginList.getUser().getLast_name().trim().equals("")) {
                                        LoginList.getUser().getPicture_token().trim();
                                    }
                                }

                                if(LoginList.getUnit().getCard_type().equals("membership_card")){
                                    Intent intent = new Intent(LoginActivity.this, MemberActivity.class);
                                    startActivity(intent);
                                }else{
                                    Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                                    startActivity(intent);
                                }

                            }

                            @Override
                            public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {
                                Context context = getApplicationContext();
                                CharSequence text = t.getMessage();
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        });
                    });
        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        }
        return true;
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }
    private void sendRegistrationToServer(String fcm_token, String auth_token) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        //local eller base
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(KVTVariables.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        UserAPI userapi = retrofit.create(UserAPI.class);

        UserDevice userDevice = new UserDevice(fcm_token);
        userapi.postToken(userDevice, auth_token).enqueue(new Callback<Token>() {
            @Override
            public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
            }

            @Override
            public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t) {

            }
        });
    }

    public boolean hasActiveInternetConnection2(){
        try{
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if(connectivityManager != null){
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
            return networkInfo != null && networkInfo.isConnected();

        }catch (NullPointerException e){
            return false;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.button_start_verification) {
            if (!validatePhoneNumber()) {
                return;
            }
            if (!hasActiveInternetConnection2()) {
                Toast.makeText(this, R.string.noInternet, Toast.LENGTH_LONG).show();
                return;
            }
            ///////hide keyboard start
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);


            inputManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


            /////////hide keyboard end


            mStatusText.setText(R.string.Autoriserer);
            progressBar.setVisibility(View.VISIBLE);
            sendVerificationCode(landskode.getText().toString() + mPhoneNumberField.getText().toString());
        }
        else if (id == R.id.button_verify_phone) {
            String code = mVerificationField.getText().toString();
            int selectedWhite = Color.rgb(0, 0, 0);
            if (TextUtils.isEmpty(code)) {
                mVerificationField.setTextColor(selectedWhite);
                mVerificationField.setError("Cannot be empty.");
                return;
            }
            if (!mVerificationInProgress) {
                mVerificationField.setError("Send code before login");
                return;
            }
            try {
                verifyPhoneNumberWithCode(mVerificationId, code);
            } catch (NullPointerException e) {
                Toast.makeText(this, R.string.Skrivinn, Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.button_resend) {
            resendVerificationCode(mPhoneNumberField.getText().toString(), mResendToken);
        }
        else if (id == R.id.sign_out_button) {
            signOut();
        }
    }
}
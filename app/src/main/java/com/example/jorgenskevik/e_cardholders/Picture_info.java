package com.example.jorgenskevik.e_cardholders;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.system.ErrnoException;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.jorgenskevik.e_cardholders.Variables.KVTVariables;
import com.example.jorgenskevik.e_cardholders.models.SessionManager;
import com.example.jorgenskevik.e_cardholders.models.User;
import com.example.jorgenskevik.e_cardholders.remote.UserAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jorgenskevik on 22.07.2018.
 */


public class Picture_info extends Activity {
    TextView button_back, continue_picture, pick_photo, information_picture; //textview_crop;
    ImageView profil_picture;
    private Uri mCropImageUri;
    String authToken, fourDigits;
    HashMap<String, String> userDetails, user, unit_member_ship;
    SessionManager sessionManager;
    Uri imageUri;
    String photo_phat;
    User get_user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_photo);

        button_back = (TextView) findViewById(R.id.back_button);
        continue_picture = (TextView) findViewById(R.id.done_button);
        profil_picture = (ImageView) findViewById(R.id.sircle);
        pick_photo = (TextView) findViewById(R.id.pick_photo);
        information_picture = (TextView) findViewById(R.id.this_is_how);
        sessionManager = new SessionManager(getApplicationContext());


            button_back.setOnClickListener(v -> {
                Intent back = new Intent(Picture_info.this, BarCodeActivity.class);
                startActivity(back);
            });

        pick_photo.setOnClickListener(v -> startActivityForResult(getPickImageChooserIntent(), 200));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            imageUri = getPickImageResultUri(data);

            boolean requirePermissions = false;
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    isUriRequiresPermissions(imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }

            if (!requirePermissions) {

                profil_picture.setImageURI(imageUri);
                android.net.Uri imageUri = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                assert imageUri != null;
                android.database.Cursor cursor = getContentResolver().query(imageUri, filePath, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePath[0]);
                String mediaPath = cursor.getString(columnIndex);
                cursor.close();

                sessionManager.setMedia_path(mediaPath);

                information_picture.setText(R.string.your_picture);
                continue_picture.setTextColor(ContextCompat.getColor(this, R.color.logobluecolor));
                pick_photo.setTextColor(ContextCompat.getColor(this, R.color.line_color));

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            profil_picture.setImageURI(mCropImageUri);
            information_picture.setText(R.string.your_picture);
            continue_picture.setTextColor(ContextCompat.getColor(this, R.color.logobluecolor));
            pick_photo.setTextColor(ContextCompat.getColor(this, R.color.line_color));

        } else {
            Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    public void onContinue(View view) {

        if (profil_picture.getDrawable() == null){
            Toast.makeText(getApplicationContext(), R.string.set_picture, Toast.LENGTH_LONG).show();
            return;
        }
        sessionManager = new SessionManager(getApplicationContext());
        userDetails = sessionManager.getUserDetails();
        unit_member_ship = sessionManager.getUnitMemberDetails();
        authToken = "token " + userDetails.get(SessionManager.KEY_TOKEN);

        fourDigits = userDetails.get(SessionManager.KEY_PICTURETOKEN);
        user = sessionManager.getMedia_path();
        photo_phat = user.get(SessionManager.KEY_MEDIA_PATH);

        //assert photo_phat != null;
        if(photo_phat != null){
            final File file = new File(photo_phat);
            String mimeType = getMimeType(file);

            RequestBody reqFile = RequestBody.create(MediaType.parse(mimeType), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), reqFile);
            RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), fourDigits);

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(KVTVariables.getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            UserAPI userapi = retrofit.create(UserAPI.class);
            userapi.postPicture(authToken, body,name).enqueue(new Callback<User>() {

                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (response.isSuccessful()){
                        get_user = response.body();
                        SessionManager sess = new SessionManager(getApplicationContext());

                        sess.updatePicture(get_user.getPicture());
                        sess.updatePath(photo_phat);
                        sess.updatePictureToken("BRUKT");
                        sess.update_boolean(true);
                        get_user.setHas_set_picture(true);

                        Intent i = new Intent(Picture_info.this, UserActivity.class);
                        startActivity(i);

                    }else{
                        Context context = getApplicationContext();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, R.string.updatePicture, duration);
                        toast.show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, R.string.PictureNotUpdated, duration);
                    toast.show();
                }
            });
        }else{
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, R.string.PictureNotUpdated, duration);
            toast.show();
        }
    }

    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (Objects.requireNonNull(intent.getComponent()).getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[0]));

        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }


    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            assert stream != null;
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getMimeType(File url) {
        String type = null;
        String test = String.valueOf(url);
        test = test.toLowerCase();
        String extension = MimeTypeMap.getFileExtensionFromUrl(test);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }if(type == null){
            type = "image/*";
        }
        return type;
    }
}

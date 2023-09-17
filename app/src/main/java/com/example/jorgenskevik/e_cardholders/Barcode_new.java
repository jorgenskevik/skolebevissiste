package com.example.jorgenskevik.e_cardholders;

/**
 * Created by jorgenskevik on 21.07.2018.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.jorgenskevik.e_cardholders.models.SessionManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Bar code activity.
 */
public class Barcode_new extends Activity {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.barcode_new);

        ImageView barcode;
        Button cancel;
        TextView student_id;

        student_id = (TextView) findViewById(R.id.student_id);
        barcode = (ImageView) findViewById(R.id.imageView4);
        cancel = (Button) findViewById(R.id.button2);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // get user data from session
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> unit_membership = sessionManager.getUnitMemberDetails();
        String studentIDString = unit_membership.get(SessionManager.KEY_STUDENTNUMBER);
        // name

        student_id.setText(studentIDString);

        // barcode image
        Bitmap bitmap;
        try {

            bitmap = encodeAsBitmap(studentIDString);
            barcode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        cancel.setOnClickListener(view -> {
            Intent intent = new Intent(Barcode_new.this, UserActivity.class);
            startActivity(intent);
        });

        /*
      The Session.
     */
        double widthSize = 0.9;
        double heightSize = 0.7;
        getWindow().setLayout((int) (width* widthSize) ,(int) (height* heightSize));
    }


    /**
     * Encode as bitmap bitmap.
     *
     * @return the bitmap
     * @throws WriterException the writer exception
     */
    Bitmap encodeAsBitmap(String contentsToEncode) throws WriterException {
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, BarcodeFormat.CODE_39, 700, 200, hints);
        } catch (IllegalArgumentException iae) {
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }
}



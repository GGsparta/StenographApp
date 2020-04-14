package com.somestudents.steganographapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10;
    private int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkPermissions())
            return;


        Uri imagesURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        Cursor cursor = getContentResolver().query(imagesURI, new String[] {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE
        }, "", null, sortOrder);

        if(cursor == null) return;

        if(cursor.moveToFirst()/* && cursor.moveToNext()*/) {
            // Init
            Bitmap refImage = BitmapFactory.decodeFile(cursor.getString(0));
            ImageView view = findViewById(R.id.image);

            Steganograph steganograph = new Steganograph();

            Bitmap newImage = null;
            try {
                newImage = steganograph.encodePicture(refImage,
                        "Hello"
                                .toCharArray());
            } catch (ImageTooSmallException imageTooSmallException) {
                imageTooSmallException.printStackTrace();
            }

            assert newImage != null;
            String hiddenText = steganograph.decodePicture(newImage);

            TextView text = findViewById(R.id.text);
            view.setImageBitmap(newImage);
            text.setText(hiddenText);
        }

        cursor.close();

    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
                return false;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                return true;
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
                return false;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                return true;
            }
        }

        return true;
    }
}

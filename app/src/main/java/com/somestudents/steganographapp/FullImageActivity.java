package com.somestudents.steganographapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

public class FullImageActivity extends AppCompatActivity {

    EditText editText;
    ImageView imageView;

    Steganograph steganograph;
    Bitmap image;
    File imgFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        //on récupère l'intent venant de l'actrivité principale
        Intent i = getIntent();
        steganograph = new Steganograph();

        //on récupère l'image via son adresse sous la forme d'un fichier (l'appli ne reconnait pas encore que c'est une image)
        imgFile = new File(i.getExtras().getString("path"));
        imageView = findViewById(R.id.fullImage);
        if (!imgFile.exists()) {
            finish();
            return;
        }

        //on décode le fichier sous un fichier bitmap (fichier image reconnu par l'appli
        image = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        //on affiche l'image au format bitmap dans l'imageview
        imageView.setImageBitmap(image);

        //on recupère le texte de l'image et on l'écrit dans l'EditText
        editText = findViewById(R.id.edit_text);

        String t = steganograph.decodePicture(image);
        editText.setText(t);
    }

    /*cette fonction est appelé lorsque le bouton apply est appuyé*/
    public void SetMessage(View view) {
        // Check permissions
        if(!checkPermissions()) {
            Toast.makeText(this, "Impossible to encode without user permission", Toast.LENGTH_SHORT).show();
            return;
        }

        // Try encoding
        long startTime = System.currentTimeMillis();
        try {
            image = steganograph.encodePicture(image, editText.getText().toString().toCharArray());
            //imageView.setImageBitmap(image);
        } catch (ImageTooSmallException e) {
            e.printStackTrace();
            Toast.makeText(this, "Image too small for encoding!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, String.format(Locale.CANADA, "Text encoded in %dms", System.currentTimeMillis() - startTime), Toast.LENGTH_SHORT).show();

        // Save file
        try {
            SaveBitmap(image);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save the picture", Toast.LENGTH_SHORT).show();
            return;
        }


        // Return to main activity
        finish();
    }

    private void SaveBitmap(Bitmap result) throws IOException {
        // Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        // Write the bytes in file
        String path = imgFile.getAbsolutePath();
        imgFile.delete();

        FileOutputStream fos = new FileOutputStream(imgFile, false);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
                return false;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                return true;
            }
        }

        return true;
    }
}

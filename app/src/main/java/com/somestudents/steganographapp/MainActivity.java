package com.somestudents.steganographapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public ArrayList<String> AllPath = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //on check si on a les permission pour accéder aux images de la carte sd de l'appareil
        if (!checkPermissions()) {
            Toast.makeText(this, "Files Storage access is needed for this application", Toast.LENGTH_SHORT).show();
            return;
        }

        //Initialisation de la gridview
        RecyclerView galleryView = findViewById(R.id.galleryView);
        galleryView.setLayoutManager(new GridLayoutManager(this, 3));

        //si on a la permission, on récupère la liste de toutes les images puis on les affiche
        final ImageAdapter imgs = new ImageAdapter(this);
        //adresse des images
        imgs.generatePathOfImages(null);
        imgs.setOnItemClickListener(new ImageAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(getApplicationContext(), FullImageActivity.class);
                i.putExtra("path", (String) imgs.getItem(position));
                startActivity(i);
            }
        });

        //utilise l'imageAdapter pour afficher chaque image dans la grille
        galleryView.setAdapter(imgs);
        galleryView.setHasFixedSize(true);
        galleryView.setItemViewCacheSize(20);
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
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                return true;
            }
        }

        return true;
    }
}

package com.somestudents.steganographapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public ArrayList<String> AllPath= new ArrayList<String>();
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialisation de la gridview
        GridView gridView = findViewById(R.id.gridView);

        //on check si on a les permission pour accéder aux images de la carte sd de l'appareil
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //si on a la permission, on récupère la liste de toutes les images puis on les affiche
            final ImageAdapter imgs = new ImageAdapter(this);
            //adresse des images
            imgs.PathOfImages( null);
            //utilise l'imageAdapter pour afficher chaque image dans la grille
            gridView.setAdapter(imgs);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                    Intent i = new Intent(getApplicationContext(),FullImageActivity.class);
                    i.putExtra("path", (String) imgs.getItem(position));
                    startActivity(i);
                }
            });
        }
        else //si on n'a pas les permissions
        {
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                //dans le cas où on a pas les autorisations, on ouvre la boite de dialogue qui dit que sans les permissions l'application
                // ne peut pas fonctionner + ferme l'application
                Toast.makeText(this, "Files Storage access is needed for this application", Toast.LENGTH_SHORT).show();
            }
            //on demande la permission
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }
    }


}

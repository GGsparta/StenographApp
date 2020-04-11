package com.somestudents.steganographapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaTimestamp;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class FullImageActivity extends AppCompatActivity {

    EditText editText;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        //on récupère l'intent venant de l'actrivité principale
        Intent i = getIntent();

        //on récupère l'information de l'intent
        int position = i.getExtras().getInt("id");

        //on affiche la bonne image dans l'ImageView
        ImageAdapter adapter = new ImageAdapter(this);
        imageView = (ImageView) findViewById(R.id.fullImage);
        imageView.setImageResource(adapter.images[position]);

        //on recupère le texte de l'image et on l'écrit dans l'EditText
        editText = findViewById(R.id.edit_text);
        editText.setText(GetEncryptedMessage());
    }

    /*cette fonction est appelé lorsque le bouton apply est appuyé*/
    public void SetMessage(View view){
        SetEncryptedMessage(editText.getText().toString());
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }


    /*cette fonction sert à encrypter une string dans un image*/
    public void SetEncryptedMessage(String str){
        //TODO
    }

    /*cette fonction récupère un message encrypter dans une image*/
    public String GetEncryptedMessage(){
        //TODO
        return "toto";
    }
}

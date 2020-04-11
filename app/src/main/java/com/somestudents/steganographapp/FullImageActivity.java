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
        Intent i = getIntent();
        int position = i.getExtras().getInt("id");
        ImageAdapter adapter = new ImageAdapter(this);
        imageView = (ImageView) findViewById(R.id.fullImage);
        imageView.setImageResource(adapter.images[position]);

        editText = findViewById(R.id.edit_text);
        editText.setText(GetEncryptedMessage());
    }

    public void SetMessage(View view){
        SetEncryptedMessage(editText.getText().toString());
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }



    public void SetEncryptedMessage(String str){

    }

    public String GetEncryptedMessage(){
        return "toto";
    }
}

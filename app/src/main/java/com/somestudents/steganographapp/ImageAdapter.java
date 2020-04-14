package com.somestudents.steganographapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    //la liste des adresses de toutes les images
    private ArrayList<String> AllPathPicture = new ArrayList<>();

    private Context context;

    ImageAdapter(Context c){ context = c; }


    @Override
    public int getCount() { return AllPathPicture.size(); }
    @Override
    public Object getItem(int position) {return AllPathPicture.get(position);}
    @Override
    public long getItemId(int i) { return 0;}

     @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        //on initialise une imageview en fonction du context
        ImageView imageView = new ImageView(context);

        //on récupère l'image via son adresse sous la forme d'un fichier (l'appli ne reconnait pas encore que c'est une image)
         File imgFile = new  File(AllPathPicture.get(position));
         if(imgFile.exists()){ // si le fichier existe
             //on décode le fichier sous un fichier bitmap (fichier image reconnu par l'appli
             Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
             //on affiche l'image au format bitmap dans l'imageview
             imageView.setImageBitmap(myBitmap);
         }
         //on centre l'image dans le carré de façon à ce qu'elle prenne tout l'espace sur sa vignette quitte à être coupé
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

         //taille de la vignette
         imageView.setLayoutParams(new GridView.LayoutParams( viewGroup.getWidth()/3, viewGroup.getWidth()/3));


        //on retourne l'image nouvellement lue
        return imageView;
    }

// méthode qui permet de remplir la list AllPathPicture pour le reste des opérations
    void PathOfImages(String criteria){
        ContentResolver cr = context.getContentResolver();
        Uri imgsURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Images.Media.TITLE +" ASC";

        Cursor cursor = cr.query(imgsURI, new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME}, criteria, null, sortOrder);

        assert cursor != null;
        if(cursor.moveToFirst()){
            do{
                if(cursor.getString(0)!=null){
                    String imagePath = cursor.getString(0);
                    AllPathPicture.add(imagePath);
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

}

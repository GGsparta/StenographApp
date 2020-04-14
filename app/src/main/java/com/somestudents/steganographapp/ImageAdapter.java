package com.somestudents.steganographapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private ItemClickListener mClickListener;
    private LayoutInflater mInflater;

    //la liste des adresses de toutes les images
    private ArrayList<String> AllPathPicture = new ArrayList<>();

    private Context context;


    ImageAdapter(Context c){
        context = c;
        mInflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //on récupère l'image via son adresse sous la forme d'un fichier (l'appli ne reconnait pas encore que c'est une image)
        File imgFile = new  File(AllPathPicture.get(position));
        if(imgFile.exists()){ // si le fichier existe
            //on décode le fichier sous un fichier bitmap (fichier image reconnu par l'appli
            Bitmap myBitmap = decodeSampledBitmapFromFile(imgFile, 150, 150);
            //on affiche l'image au format bitmap dans l'imageview
            holder.setImage(myBitmap);
        }
    }
    String getItem(int i) { return AllPathPicture.get(i);}
    @Override
    public int getItemCount() { return AllPathPicture.size();}


    // méthode qui permet de remplir la list AllPathPicture pour le reste des opérations
    void generatePathOfImages(String criteria){
        ContentResolver cr = context.getContentResolver();
        Uri imgsURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED +" DESC";

        Cursor cursor = cr.query(imgsURI, new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME}, criteria, null, sortOrder);

        assert cursor != null;
        if(cursor.moveToFirst()){
            do{
                if(cursor.getString(0)!=null){
                    String imagePath = cursor.getString(0);
                    AllPathPicture.add(imagePath);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageItem;

        ViewHolder(View itemView) {
            super(itemView);
            imageItem = itemView.findViewById(R.id.imageItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        void setImage(Bitmap image) {
            imageItem.setImageBitmap(image);
        }
    }


    // allows clicks events to be caught
    void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    public static Bitmap decodeSampledBitmapFromFile(File file, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}

package com.somestudents.steganographapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

class Steganograph {
    /*
    Les informations seront codées au début de l'image, sur les 2 bits faibles de chaque octet:
        - 32 bits definissant signature de l'application, afin de savoir si l'image a été enregistrée par l'application
        - 8 bits definissant la taille du texte
        -
     */

    Bitmap encodePicture(Bitmap refImage, String text) {
        int width = refImage.getWidth();
        int height = refImage.getHeight();

        int size = refImage.getRowBytes() * refImage.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        refImage.copyPixelsToBuffer(byteBuffer);

        byte[] bytes = byteBuffer.array();

        for(int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte) ((bytes[i] & 254) | 1);
        }

        Bitmap.Config configBmp = Bitmap.Config.valueOf(refImage.getConfig().name());
        Bitmap newImage = Bitmap.createBitmap(width, height, configBmp);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        newImage.copyPixelsFromBuffer(buffer);
        return newImage;
    }

    String decodePicture(Bitmap refImage) {
        return "null";
    }
}

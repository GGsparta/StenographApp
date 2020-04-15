package com.somestudents.steganographapp;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;
import java.util.Locale;

class Steganograph {
    /*
    Les informations seront codées au début de l'image, sur les bits les plus faibles de chaque octet:
        - 31 bits significatifs definissant la SIGNATURE de l'application, afin de savoir si l'image a été enregistrée par l'application (1 bit par octet)
        - 7 bits significatifs definissant le RATIO d'encodage (1 bit par octet)
        - 31 bits significatifs definissant la TAILLE du texte (1-7 bits par octet)
        - 15 bits significatifs * taille du TEXTE (1-7 bits par octet)

    Le format d'image pris en compte est le RGBA_8888 (4 octets pour un pixel)
     */
    private final static int MAX_ENCODED_BITS_PER_BYTE = 7;
    private final static int APP_SIGNATURE = 6719719; // Si c'est un oeuf, c'est un oeuf, non?
    private final static int SIZE_OF_INT = 32;
    private final static int SIZE_OF_CHAR = 16;
    private final static int SIZE_OF_BYTE = 8;
    private final static int[] POWERS_OF_2 = generatePowersOf2();

    private int currentBitIndex, byteIndex, nbEncodedBitsPerByte = 1;

    /*

    PUBLIC METHODS

     */
    Bitmap encodePicture(Bitmap refImage, char[] text) throws ImageTooSmallException {
        // Init
        int
                width = refImage.getWidth(),
                height = refImage.getHeight(),
                size = refImage.getRowBytes() * refImage.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        refImage.copyPixelsToBuffer(byteBuffer);
        byte[] bytes = byteBuffer.array();
        reinitializeCursor(true);

        // Signing
        if(size < SIZE_OF_INT + SIZE_OF_BYTE)
            throw new ImageTooSmallException();
        writeBits(bytes, toBitArray(APP_SIGNATURE, SIZE_OF_INT));

        // Defining data rate
        while (2 * SIZE_OF_INT + text.length * SIZE_OF_CHAR // Content to encode
                > (size - SIZE_OF_BYTE) * nbEncodedBitsPerByte // Space available beside data rate information
        && nbEncodedBitsPerByte <= MAX_ENCODED_BITS_PER_BYTE)
            ++nbEncodedBitsPerByte;
        if(nbEncodedBitsPerByte > MAX_ENCODED_BITS_PER_BYTE)
            throw new ImageTooSmallException();
        writeBits(bytes, toBitArray(nbEncodedBitsPerByte, SIZE_OF_BYTE));
        reinitializeCursor(false);

        System.out.println(String.format(Locale.CANADA, "Using %d bits per byte", nbEncodedBitsPerByte));

        // Defining size
        writeBits(bytes, toBitArray(text.length, SIZE_OF_INT));

        // Encoding
        for (char c : text)
            writeBits(bytes, toBitArray(c, SIZE_OF_CHAR));

        // Creating new picture
        Bitmap.Config configBmp = Bitmap.Config.valueOf(refImage.getConfig().name());
        Bitmap newImage = Bitmap.createBitmap(width, height, configBmp);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        newImage.copyPixelsFromBuffer(buffer);
        return newImage;
    }

    String decodePicture(Bitmap refImage) {
        // Init
        int size = refImage.getRowBytes() * refImage.getHeight();
        StringBuilder result = new StringBuilder();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        refImage.copyPixelsToBuffer(byteBuffer);
        byte[] bytes = byteBuffer.array();
        reinitializeCursor(true);

        System.out.println(String.format(Locale.CANADA, "Image format: %s", refImage.getConfig().toString()));

        // Checking signature
        if(readNumber(bytes, SIZE_OF_INT) != APP_SIGNATURE)
            return result.toString();

        // Reading data rate
        if(size <= SIZE_OF_BYTE
                || (nbEncodedBitsPerByte = readNumber(bytes, SIZE_OF_BYTE)) > MAX_ENCODED_BITS_PER_BYTE
                || nbEncodedBitsPerByte < 1
                || (size - SIZE_OF_BYTE) * nbEncodedBitsPerByte < 2 * SIZE_OF_INT)
            return result.toString();
        reinitializeCursor(false);

        // Reading text size
        int textSize = readNumber(bytes, SIZE_OF_INT);
        if((size - SIZE_OF_BYTE) * nbEncodedBitsPerByte < 2 * SIZE_OF_INT + textSize * SIZE_OF_CHAR)
            return result.toString();

        // Decoding
        for (int i = 0; i < textSize; i++)
            result.append((char) readNumber(bytes, SIZE_OF_CHAR));

        return result.toString();
    }




    /*

    PRIVATE: READING METHODS

     */
    private void reinitializeCursor(boolean resetRate) {
        if(resetRate)
            nbEncodedBitsPerByte = 1;
        currentBitIndex = nbEncodedBitsPerByte - 1;
        byteIndex = resetRate ? 0 : (SIZE_OF_BYTE + SIZE_OF_INT);
    }

    private int readNumber(byte[] bytes, int typeSize) {
        int n = 0;
        for(int j = typeSize - 1; j >= 0; --j) {
            if(j != typeSize - 1 && getBit(bytes[byteIndex], currentBitIndex)) {
                n |= POWERS_OF_2[j];
            }
            goNextBit();
        }
        return n;
    }

    private void goNextBit() {
        --currentBitIndex;
        if (currentBitIndex < 0) {
            ++byteIndex;
            currentBitIndex = nbEncodedBitsPerByte - 1;
        }
    }

    private boolean getBit(byte refByte, int index) {
        return (refByte & POWERS_OF_2[index]) != 0;
    }



    /*

    PRIVATE: WRITING METHODS

     */
    private void writeBits(byte[] bytes, boolean[] values) throws ImageTooSmallException {
        for (boolean bit : values) {
            if (byteIndex >= bytes.length) throw new ImageTooSmallException();
            bytes[byteIndex] = setBit(bytes[byteIndex], currentBitIndex, bit);

            goNextBit();
        }
    }

    private byte setBit(byte number, int index, boolean value) { // index: from right to left
        if(index >= SIZE_OF_BYTE)
            return number;

        byte bit = (byte) POWERS_OF_2[index];
        boolean isBitOne = (number & bit) != 0;

        // Avoid more computing if value already set (50% chances)
        if(isBitOne == value)
            return number;

        // Otherwise value is different and changes are applied
        if(value) number |= bit;
        else number &= (Byte.MAX_VALUE - bit) | (number & Byte.MIN_VALUE);

        return number;
    }



    /*

    PRIVATE: CONVERSION/OPTIMIZATION METHODS

     */
    private boolean[] toBitArray(int number, int typeSize) {
        boolean[] result = new boolean[typeSize];
        for(int i = 1; i < result.length; ++i)
            result[i] = (number & POWERS_OF_2[result.length - 1 - i]) != 0;
        return result;
    }

    private static int[] generatePowersOf2() {
        int[] result = new int[SIZE_OF_INT - 1];
        for(int i = 0; i < result.length; i++)
            result[i] = (int) Math.pow(2, i);
        return result;
    }
}

package com.example.tsundere.teamhackage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

//Класс состоящий из методов для преобразования Bitmap в байтовый массив и обратно

public class ImageDecoder {

    //Преобразовать Bitmap в байтовый массив
    public static byte[] bitmapToByteArray(Bitmap img) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 75, stream);
        return stream.toByteArray();
    }

    //Получить Bitmap из байтового массива без сжатия
    public static Bitmap bitmapFromByteArrayHighQ(byte[] arr) {
        return BitmapFactory.decodeByteArray(arr, 0, arr.length);
    }

    //Получить Bitmap из байтового массива с сжатием
    public static Bitmap bitmapFromByteArrayLowQ(byte[] arr) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(arr, 0, arr.length, options); //Получить характеристики изображения
        int maxDimSize = Math.max(options.outWidth, options.outHeight);
        int scale = Math.max(1, Math.round((float)maxDimSize / (float) 640)); //Коэффициент сжатия изображения
        options = new BitmapFactory.Options();
        options.inSampleSize = scale; //Устанавливаем коэффициент сжатия
        options.inPreferredConfig = Bitmap.Config.RGB_565; //Устанавливаем цветовой конфиг
        return BitmapFactory.decodeByteArray(arr, 0, arr.length, options);
    }
}

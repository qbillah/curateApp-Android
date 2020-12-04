package com.example.curatetest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ImageCompress {

    //SIMPLE IMAGE COMPRESSION CLASS FOR IMAGES BEING UPLOADED TO FIREBASE

    Bitmap toCompress;
    Context ctx;
    String UiD;

    public ImageCompress(Bitmap b , Context c , String u){
        this.toCompress = b;
        this.UiD = u;
    }

    public Bitmap compressImage() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        toCompress.compress(Bitmap.CompressFormat.JPEG, 70, baos);// 100baos
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 100kb,
            baos.reset();// baosbaos
            toCompress.compress(Bitmap.CompressFormat.JPEG, options, baos);// options%baos
            options -= 10;// 10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(
                baos.toByteArray());// baosByteArrayInputStream
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// ByteArrayInputStream

        return bitmap;
    }

}

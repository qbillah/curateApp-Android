package com.example.curatetest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

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
        this.ctx = c;
        this.UiD = u;
    }

    public Uri CompressToFirebase(){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        toCompress.compress(Bitmap.CompressFormat.JPEG, 20, bytes);
        String path = MediaStore.Images.Media.insertImage(ctx.getContentResolver(), toCompress ,"ppf/"+UiD+"ppf",null);
        Uri toUploadUri = Uri.parse(path);
        return toUploadUri;
    }

}

package com.example.curatetest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

// PERMISSION MANAGER CLASS
/*
    USE THIS TO CHECK PERMISSIONS

    HOW TO USE -
    1. WE ONLY NEED READING EXTERNAL AND WRITING EXTERNAL STORAGE
    2. INIT THE CONSTRUCTOR BY GETTING CONTEXT ON CREATE
    3. IN ACTIVITY GET IF PERMISSION IS SET USING THE 2 BOOLEAN FUNCTIONS
    4. IF THOSE RETURN FALSE
    5. PLUG THE VALUES AND THE ACTIVITY INTO THE REQUEST PERMISSION FUNCTION
 */

public class PermissionHelper {
    Context ctx;
    public PermissionHelper(Context c){
        this.ctx = c;
    }
    public boolean hasReadExternalStoragePermission(){
        boolean has = ActivityCompat.checkSelfPermission(ctx , Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return has;
    }
    public boolean hasWriteExternalStoragePermission(){
        boolean has = ActivityCompat.checkSelfPermission(ctx , Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return has;
    }
    public void requestPermissions(Boolean read , Boolean write , Activity a){
        ArrayList<String> permissionsToRequest = new ArrayList<String>();
        if(!read){
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(!write){
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionsToRequest.isEmpty()){
            String requests[]= permissionsToRequest.toArray(new String[permissionsToRequest.size()]);
            ActivityCompat.requestPermissions(a , requests , 0);
        }
    }
}

package com.example.curatetest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public Session(Context ctx){
        sharedPreferences = ctx.getSharedPreferences("APP_KEY" , 0);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public void setLogin(boolean login){
        editor.putBoolean("KEY_LOGIN" , login);
        editor.commit();
    }

    public boolean getLogin(){
        return sharedPreferences.getBoolean("KEY_LOGIN" , false);
    }

    public void setUserEmail(String userEmail){
        editor.putString("KEY_USER_EMAIL" , userEmail);
        editor.commit();
    }

    public String getUserEmail(){
        return sharedPreferences.getString("KEY_USER_EMAIL" , "");
    }

    public void setUserName(String userName){
        editor.putString("KEY_USER_NAME" , userName);
        editor.commit();
    }

    public String getUserName(){
        return sharedPreferences.getString("KEY_USER_NAME" , "");
    }

}

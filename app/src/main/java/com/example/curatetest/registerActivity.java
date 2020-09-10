package com.example.curatetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class registerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void goHome(View V){
        Intent curateHome = new Intent(this,MainActivity.class);
        startActivity(curateHome);
    }

    //USERNAME & PASSWORD VALIDATION - ADD HERE

}
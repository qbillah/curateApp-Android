package com.example.curatetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class registerActivity extends AppCompatActivity {

    private Session session;

    private EditText getUsername;
    private EditText getPassword;

    private String emailAddress;
    private String username;
    private String password;

    Session sessionManager;

    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //System.out.println(sessionManager.getUserEmail());
    }

    public void signUp(View V){
        getUsername = (EditText) findViewById(R.id.userEmail);
        getPassword = (EditText) findViewById(R.id.password);
        username = getUsername.getText().toString();
        password = getPassword.getText().toString();

        if(validateUsername(username)){
            if(validatePassword(password)){
                registerAndLogin(username);
            }else if(!validatePassword(password)){
                System.out.println("err1");
            }
        }else if(!validateUsername(username)){
            System.out.println("err2");
        }
    }


    public void goHome(View V){
        Intent curateHome = new Intent(this,MainActivity.class);
        startActivity(curateHome);
    }

    //USERNAME & PASSWORD VALIDATION - ADD HERE

    private boolean validateUsername(String username){
        //RUN DB FOR USERNAME VALIDATION
        return true;
    }

    private boolean validatePassword(String password){
        boolean flag = false;
        int passwordLen = password.length();
        if(passwordLen > 8){
            if(checkForUpper(password)){
                flag = true;
            }else if(!checkForUpper(password)){
                flag = false;
            }
        }else{
            flag = false;
        }
        return flag;
    }

    //CHECKS FOR 1 UPPERCASE CHARACTER IN PASSWORD STRING

    private boolean checkForUpper(String password){
        boolean flag = false;
        for(int i = 0; i < password.length(); i++){
            if(Character.isUpperCase(password.charAt(i))){
                flag = true;
                break;
            }else{
                flag = false;
            }
        }
        return flag;
    }

    private void registerAndLogin(String username){
        //TWO PARTS
        //1. REGISTER USER INTO DB
        //2. START SESSION AND LOG IN USER

        //PART 2 - SORT OF
        Session sessionManager = new Session(getApplicationContext());
        sessionManager.setUserName(username);
        sessionManager.setLogin(true);

        System.out.println("Logged In");

        //REDIRECT TO USER DASHBOARD AFTER USER IS REGISTERED AND LOGGED IN
        Intent curateDashboard = new Intent(this , dashboard.class);
        startActivity(curateDashboard);
    }

}

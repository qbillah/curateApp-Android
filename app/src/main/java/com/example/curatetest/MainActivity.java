package com.example.curatetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText getUserEmail;
    private TextView blankEmailAlert;
    private String userEmail;

    Session sessionManager;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EMAIL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new Session(getApplicationContext());
    }

    //ON START CHECK IF USER IS LOGGED IN

    @Override
    protected void onStart(){
        super.onStart();
        boolean isLoggedIn = sessionManager.getLogin();
        if(isLoggedIn){
            Intent curateDashboard = new Intent(this , dashboard.class);
            startActivity(curateDashboard);
        }else if(!isLoggedIn){
            System.out.println("Not Logged In");
        }
    }

    public void getUserEmail (View view){
        //GET USER EMAIL
        //CHECK THRU DATABASE IF EMAIL IS REGISTERED
        //IF REGISTERED TAKE INTO APP
        //IF NOT, TAKE TO SIGN UP PAGE

        getUserEmail = (EditText) findViewById(R.id.userEmail);
        blankEmailAlert = (TextView) findViewById(R.id.registerAlert);
        userEmail = getUserEmail.getText().toString();

        //IF EMAIL IS NOT BLANK THEN VALIDATE EMAIL
        if(userEmail.isEmpty()){
            blankEmailAlert.setVisibility(View.VISIBLE);
        }else{

            if(isValidEmail(userEmail) == true){

                //REPLACE LATER - SHOULD CHECK DB FOR EMAIL ADDRESS
                //IF EMAIL IS REGISTERED TAKE TO SIGN IN VIEW
                //IF EMAIL ISN'T REGISTERED TAKE TO SIGN UP VIEW

                //ADD EMAIL TO SHARED PREFERENCES ****

                saveEmailPref(userEmail);

                //REPLACE WITH ACTUAL CALL TO DATABASE
                if(userEmail.equals("yasinbillahdesigns@gmail.com")){
                    //RETURN PASSWORD VIEW
                    openSignInActivity();
                }else{
                    // RETURN REGISTER VIEW
                    openRegisterActivity();
                }

            }else if(isValidEmail(userEmail) == false){
                blankEmailAlert.setText("Must be a valid email address.");
                blankEmailAlert.setVisibility(View.VISIBLE);
            }

        }

    }

    public void forgotPassword (View view){
        //TAKE TO FORGOT PASSWORD VIEW
    }

    boolean isValidEmail(CharSequence userEmail){
        //EMAIL VALIDATION FUNCTION
        return Patterns.EMAIL_ADDRESS.matcher(userEmail).matches();
    }

    public void openSignInActivity(){
        Intent signIn = new Intent(this , signInActivity.class);
        startActivity(signIn);
    }

    public void openRegisterActivity(){
        Intent registerNewUser = new Intent(this , registerActivity.class);
        startActivity(registerNewUser);
    }

    public void saveEmailPref(String email){
        sessionManager.setUserEmail(email);
    }

}

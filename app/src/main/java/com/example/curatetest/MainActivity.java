package com.example.curatetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText getUserEmail;
    private TextView blankEmailAlert;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getUserEmail (View view){
        //GET USER EMAIL
        //CHECK THRU DATABASE IF EMAIL IS REGISTERED
        //IF REGISTERED TAKE INTO APP
        //IF NOT, TAKE TO SIGN UP PAGE

        getUserEmail = (EditText) findViewById(R.id.userName);
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

                //REPLACE WITH ACTUAL CALL TO DATABASE

                //ADD EMAIL TO SHARED PREFERENCES ****

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

}

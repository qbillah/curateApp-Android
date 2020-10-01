package com.example.curatetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText getUserEmail;
    private TextView blankEmailAlert;
    private String userEmail;

    private ProgressBar mainPB;

    Session sessionManager;
/*
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EMAIL = "";
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new Session(getApplicationContext());
        mainPB = (ProgressBar) findViewById(R.id.mainProgressBar);
    }

    //ON START CHECK IF USER IS LOGGED IN

    @Override
    protected void onStart(){
        super.onStart();
        //IF FIREBASE USER IS LOGGED IN TAKE TO DASHBOARD
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent curateDashboard = new Intent(this , dashboard.class);
            startActivity(curateDashboard);
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
            blankEmailAlert.setText(getString(R.string.emptyField));
        }else{

            if(isValidEmail(userEmail) == true){

                //IF EMAIL IS REGISTERED TAKE TO SIGN IN VIEW
                //IF EMAIL ISN'T REGISTERED TAKE TO SIGN UP VIEW

                //ADD EMAIL TO SHARED PREFERENCES ****
                saveEmailPref(userEmail);
                userExists(userEmail);
            }else if(isValidEmail(userEmail) == false){
                blankEmailAlert.setText(getString(R.string.emailAlert));
                blankEmailAlert.setVisibility(View.VISIBLE);
            }
        }
    }

    public void forgotPassword (){
        //TAKE TO FORGOT PASSWORD VIEW
    }

    public boolean isValidEmail(CharSequence userEmail){
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

    public void userExists(String user){
        mainPB.setVisibility(View.VISIBLE);
        mAuth.fetchSignInMethodsForEmail(user)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        mainPB.setVisibility(View.GONE);
                        boolean check = !task.getResult().getSignInMethods().isEmpty();
                        if(!check){
                            openRegisterActivity();
                        }else{
                            openSignInActivity();
                        }
                    }
                });
    }

}

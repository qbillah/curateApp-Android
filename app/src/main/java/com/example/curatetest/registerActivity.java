package com.example.curatetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class registerActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Session session;

    private EditText getUsername;
    private EditText getPassword;
    private TextView regAlert;

    private String emailAddress;
    private String username;
    private String password;
    private static final String defaultPPF = "https://firebasestorage.googleapis.com/v0/b/curateapp-72ea6.appspot.com/o/ppf%2Fdefault.png?alt=media&token=015f1044-66bf-4656-9597-c372ddc0772b";

    private Boolean validateUserFlag;

    Session sessionManager;

    private ProgressBar regiPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new Session(getApplicationContext());
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new Session(getApplicationContext());

        regiPB = (ProgressBar) findViewById(R.id.registerProgressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void signUp(View V){

        getUsername = (EditText) findViewById(R.id.userName);
        getPassword = (EditText) findViewById(R.id.password);
        username = getUsername.getText().toString();
        password = getPassword.getText().toString();
        emailAddress = sessionManager.getUserEmail();
        regAlert = (TextView) findViewById(R.id.registerAlert);

        //FIREBASE DATABASE REFERENCE
        //QUERY THE DB
        //IN THE USER CHILD CHECK IF USERNAME MATCHES EXISTING USERNAME
        //IF NOT EXIST - REGISTER
        //IF EXIST - THROW ERROR

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("users").orderByChild("username").equalTo(username);

        regiPB.setVisibility(View.VISIBLE);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // - USERNAME TAKEN
                    regAlert.setText(getString(R.string.usernameAlert));
                    regAlert.setVisibility(View.VISIBLE);
                    regiPB.setVisibility(View.GONE);
                }else{
                    //IF USERNAME IS AVAILABLE
                    //CHECK PASSWORD - IF PASSWORD MEETS REQUIREMENTS - REGISTER USER
                    if(validatePassword(password)){
                        regiPB.setVisibility(View.GONE);
                        registerAndLogin(username , emailAddress , password);
                    }else if(!validatePassword(password)){
                        // - PASSWORD DOES NOT MEET REQUIREMENTS
                        regiPB.setVisibility(View.GONE);
                        regAlert.setText(getString(R.string.passwordAlert));
                        regAlert.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                //DO SOMETHING ON DATABASE READ ERROR
            }
        });

    }

    public void goHome(View V){
        Intent curateHome = new Intent(this,MainActivity.class);
        startActivity(curateHome);
    }

    //USERNAME & PASSWORD VALIDATION - ADD HERE

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

    private void registerAndLogin(final String username , String email , String password){
        /*
            REGISTER AND LOGIN:
            1. AUTHENTICATE AND ADD USER TO FIREBASE AUTH
            2. MAKE USER CHILD FOR NEW USER WITH USER UID IN FIREBASE RDB
            3. SET SESSIONS FOR USER
            4. REDIRECT USER TO DASHBOARD VIEW
         */

        //CREATE FIREBASE AUTHENTICATED USER
        regiPB.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email , password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){



                            //GET CURRENT USER FOR REGISTERED USER
                            FirebaseUser user = mAuth.getCurrentUser();

                            //GET UID OF REGISTERED USER
                            String UID = user.getUid();
                            Log.d("UID" , UID);

                            //CREATE NEW USER CHILD WITH UID CONTAINING EMAIL / USERNAME CHILDS
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

                            Map<String, String> userData = new HashMap<String, String>();
                            userData.put("email" , emailAddress);
                            userData.put("username" , username);
                            userData.put("PPF" , defaultPPF);
                            userData.put("bio" , "Check out my fits on Curate");

                            reference.child(user.getUid()).setValue(userData);

                            regiPB.setVisibility(View.GONE);
                            regAlert.setVisibility(View.GONE);
                            openDashboard();

                        }else{
                            regAlert.setText(getString(R.string.criticalAlert1));
                            regAlert.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }

    public void openDashboard(){
        Intent curateDashboard = new Intent(this , dashboard.class);
        startActivity(curateDashboard);
    }

}

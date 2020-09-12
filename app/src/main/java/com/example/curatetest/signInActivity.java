package com.example.curatetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class signInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Session sessionManager;

    private String signInEmail;
    private String signInPass;

    private ProgressBar signInPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new Session(getApplicationContext());
        signInPB = (ProgressBar) findViewById(R.id.signInProgressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        signInEmail = sessionManager.getUserEmail();

        Button signIn = (Button) findViewById(R.id.userLogin);

        signIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                signInPass = getSignInPass();
                signInUser(signInEmail , signInPass);
            }
        });

    }

    public void goHome(View V){
        Intent curateHome = new Intent(this,MainActivity.class);
        startActivity(curateHome);
    }

    public String getSignInPass(){
        String pass = "";
        EditText getPass = (EditText) findViewById(R.id.loginPass);
        pass = getPass.getText().toString();
        return pass;
    }

    //SIGN IN VALIDATION
    public void signInUser(String email , String password){
        signInPB.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email , password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            signInPB.setVisibility(View.GONE);
                            FirebaseUser user = mAuth.getCurrentUser();
                            sessionManager.editor.clear();
                            sessionManager.editor.commit();
                            openDashboard();
                        }else{
                            signInPB.setVisibility(View.GONE);
                            TextView passwordAlert = (TextView) findViewById(R.id.signInAlert);
                            passwordAlert.setVisibility(View.VISIBLE);
                            passwordAlert.setText("Invalid Password, try again!");
                        }
                    }
                });
    }

    public void openDashboard(){
        Intent curateDashboard = new Intent(this , dashboard.class);
        startActivity(curateDashboard);
    }

}
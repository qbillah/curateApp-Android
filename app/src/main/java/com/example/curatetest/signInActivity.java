package com.example.curatetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
        forgotPassword (); //calls forgotPassword

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new Session(getApplicationContext());
        signInPB = findViewById(R.id.signInProgressBar);

    }

    @Override
    protected void onStart() {
        super.onStart();

        signInEmail = sessionManager.getUserEmail();

        Button signIn = findViewById(R.id.userLogin);

        signIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                signInPass = getSignInPass();
                if(signInPass.isEmpty()){
                    TextView passwordAlert = findViewById(R.id.signInAlert);
                    passwordAlert.setVisibility(View.VISIBLE);
                    passwordAlert.setText(getString(R.string.emptyField));
                }else if(!signInPass.isEmpty()){
                    signInUser(signInEmail , signInPass);
                }
            }
        });

    }

    public void goHome(View V){
        Intent curateHome = new Intent(this,MainActivity.class);
        startActivity(curateHome);
    }

    public String getSignInPass(){
        String pass = "";
        EditText getPass = findViewById(R.id.loginPass);
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
                            TextView passwordAlert = findViewById(R.id.signInAlert);
                            passwordAlert.setVisibility(View.VISIBLE);
                            passwordAlert.setText(getString(R.string.loginIncorrect));
                        }
                    }
                });
    }

    public void forgotPassword () {
        Button forgotTextLink = findViewById(R.id.forgotPassword);

        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPasswordDialog();

            }
        });
    }

    private void showForgotPasswordDialog() {
        //Alert Dialog, Applies alert theme
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        passwordResetDialog.setTitle("Reset Password");
        passwordResetDialog.setMessage("Enter your email and we'll send you a link to get back into your account.");

        //set layout
        LinearLayout linearLayout = new LinearLayout(this);

        //views
        final EditText resetEmail = new EditText(this);
        resetEmail.setHint("Email");
        resetEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        //this sets the length of the resetEmail input box
        resetEmail.setMinEms(16);

        linearLayout.addView(resetEmail);
        linearLayout.setPadding(50,10,50,10);

        passwordResetDialog.setView(linearLayout);

        //button: reset
        passwordResetDialog.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //input email
                String email = resetEmail.getText().toString().trim();

                //if user doesn't input email
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                signInPB.setVisibility(View.VISIBLE);
                //starts password reset process
                beginPasswordReset(email);

            }
        });
        //button: cancel
        passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //dismiss
                dialogInterface.dismiss();

            }
        });

        //show dialog
        passwordResetDialog.create().show();
    }

    private void beginPasswordReset(String email) {

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(signInActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(signInActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }

                        signInPB.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //get and show error message
                        Toast.makeText(signInActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    public void openDashboard(){
        Intent curateDashboard = new Intent(this , dashboard.class);
        startActivity(curateDashboard);
    }

}
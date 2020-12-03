package com.example.curatetest;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.Duration;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserSettingsFragment extends Fragment {

    private static final int RESULT_OK = -1;
    private FirebaseAuth mAuth;
    private StorageReference storageRef;
    private PermissionHelper permissionManager;

    private Button logout , changeBio , changePPF;
    private EditText editBio;
    private ProgressBar settingProg;

    private String userChoosenTask;

    private Uri profileURI;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserSettingsFragment newInstance(String param1, String param2) {
        UserSettingsFragment fragment = new UserSettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        permissionManager = new PermissionHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);

        editBio = (EditText) view.findViewById(R.id.editBio);
        settingProg = (ProgressBar) view.findViewById(R.id.settingsProgress);
        settingProg.setVisibility(View.GONE);

        // Set logout button

        logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        // Set current bio as hint for change bio

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("users").child(mAuth.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String bio = dataSnapshot.child("bio").getValue(String.class);
                    editBio.setHint(bio);
                }else{

                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        changeBio = (Button) view.findViewById(R.id.changeBio);
        changeBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeBio();
            }
        });

        changePPF = (Button) view.findViewById(R.id.changePPF);
        changePPF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean read = permissionManager.hasReadExternalStoragePermission();
                boolean write = permissionManager.hasReadExternalStoragePermission();
                if(!read && !write){
                    permissionManager.requestPermissions(read , write , getActivity());
                }else{
                    setPPF();
                }

            }
        });

        return view;
    }


    public void changeBio(){
        FirebaseUser user = mAuth.getCurrentUser();
        String changeBio = editBio.getText().toString();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        HashMap bio = new HashMap();
        bio.put("bio" , changeBio);
        reference.child(user.getUid()).updateChildren(bio);
        editBio.setText("");
    }

    public void setPPF(){
        final CharSequence[] options = {"Take Photo" , "Choose from Gallery" , "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change Profile Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //add permissions
                if(options[i].equals("Take Photo")){
                    userChoosenTask = "Take Photo";
                    if(true){
                        cameraIntent();
                    }
                }else if(options[i].equals("Choose from Gallery")){
                    userChoosenTask = "Choose from Gallery";
                    if(true){
                        galleryIntent();
                    }
                }else if(options[i].equals("Cancel")){
                    userChoosenTask = "Cancel";
                    if(true){
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(gallery ,  1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //USE THE LOGS TO FIND OUT REQUEST / RESULT CODES FOR CAMERA INTENT
        //Log.d("IMG" , Integer.toString(requestCode));
        //Log.d("IMG" , Integer.toString(resultCode));

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            //SET PROFILE URI TO DATA
            //CONVERT URI INTO BITMAP
            //CALL UPLOAD FUNCTION WITH BITMAP ARGUMENT
            profileURI = data.getData();
            uploadPPF(profileURI);
        }

        //ADD AN ELSE IF STATEMENT FOR TAKING PHOTO DIRECTLY FROM CAMERA ROLL

    }

    private void cameraIntent() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera , 0);
    }

    public void uploadPPF(Uri U){

        //IMAGE COMPRESSION CLASS
        //TAKES THREE ARGUMENTS - CONVERTED BITMAP IMAGE, APPLICATION CONTEXT, AND UNIQUE ID (COULD BE PROVIDED BY MAUTH)
        final StorageReference ppfRef = storageRef.child("ppf/"+mAuth.getUid()+"ppf");
        UploadTask up = ppfRef.putFile(U); //GET RETURNED URI FROM IMAGE COMPRESSION CLASS AND UPLOAD TO FIREBASE

        settingProg.setVisibility(View.VISIBLE);
        up.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                double progress = (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                ObjectAnimator animA = ObjectAnimator.ofInt(settingProg, "progress" , (int)progress);
                animA.setDuration(415);
                animA.setInterpolator(new LinearInterpolator());
                animA.start();

                //settingProg.setProgress((int)progress);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                settingProg.setProgress(0);
                settingProg.setVisibility(View.GONE);
                Snackbar.make(getView() , "Profile Picture Updated" , Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        Task<Uri> urlTask = up.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return ppfRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                    HashMap PPF = new HashMap();
                    PPF.put("PPF" , downloadUri.toString());
                    reference.child(mAuth.getUid()).updateChildren(PPF);

                    Log.d("URI" , downloadUri.toString());
                } else {
                    // Handle failures
                    // ...
                    Log.d("FBDB2" , "ERR");
                }
            }
        });
    }

    public void logout(){
        mAuth.signOut();
        Intent home = new Intent(getActivity() , MainActivity.class);
        startActivity(home);
    }

}
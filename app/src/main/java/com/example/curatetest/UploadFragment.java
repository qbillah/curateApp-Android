package com.example.curatetest;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFragment extends Fragment {

    private FirebaseAuth mAuth;
    private StorageReference storageRef;
    private PermissionHelper permissionManager;

    private static final int RESULT_OK = -1;

    private ImageButton uploadPreview;
    private Button uploadPost;
    private EditText postTags;
    private ProgressBar uploadProgress;

    private Uri postURI;

    private String unsortedTags;
    private String[] sortedTags;

    private String timeStamp;
    private String uploadedBy;

    private ArrayList<String> userBrands = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment();
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
        View view = inflater.inflate(R.layout.fragment_upload, container, false);


        uploadPreview = (ImageButton) view.findViewById(R.id.uploadPreview);
        uploadPost = (Button) view.findViewById(R.id.uploadPost);
        uploadProgress = (ProgressBar) view.findViewById(R.id.uploadProgress);
        postTags = (EditText) view.findViewById(R.id.addTags);

        uploadProgress.setVisibility(View.GONE);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("users").child(mAuth.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    uploadedBy = username;
                }else{

                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        uploadPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean read = permissionManager.hasReadExternalStoragePermission();
                boolean write = permissionManager.hasReadExternalStoragePermission();
                if(!read && !write){
                    permissionManager.requestPermissions(read , write , getActivity());
                }else{
                    cropPicture();
                }
            }
        });

        uploadPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPost();
            }
        });

        return view;
    }

    public void uploadPost(){
        final Uri toUpload = postURI;

        //IF URI IS NULL HANDLE ERROR
        if(toUpload == null){
            Toast.makeText(getContext() , "Choose Image!" , Toast.LENGTH_LONG).show();
        }else{
            //DELIMIT TAGS (,)
            //CLEAN SPACES

            if(!postTags.getText().toString().isEmpty()){

                unsortedTags = postTags.getText().toString();

                sortedTags = unsortedTags.split(",");
                for(int i = 0; i < sortedTags.length; i++){
                    sortedTags[i] = sortedTags[i].trim();
                }

                //TIMESTAMP TO ADD TO POST
                timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                timeStamp = timeStamp.replace("." , "");

                //FIREBASE STORAGE REF
                final StorageReference postRef = storageRef.child("posts/"+mAuth.getUid()+"p"+timeStamp);
                UploadTask up = postRef.putFile(toUpload);

                uploadProgress.setVisibility(View.VISIBLE);
                up.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                        double progress = (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        ObjectAnimator animA = ObjectAnimator.ofInt(uploadProgress, "progress" , (int)progress);
                        animA.setDuration(415);
                        animA.setInterpolator(new LinearInterpolator());
                        animA.start();

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadProgress.setProgress(0);
                        uploadProgress.setVisibility(View.GONE);
                        Snackbar.make(getView() , "Post Uploaded!" , Snackbar.LENGTH_SHORT)
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
                        return postRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            String childRef = user.getUid().toString() + timeStamp;

                            Uri downloadUri = task.getResult();

                            ArrayList<String> tagList = new ArrayList<>();
                            Collections.addAll(tagList , sortedTags);

                            //UPLOAD POST + POST INFO TO DB
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
                            HashMap post = new HashMap();
                            //ADD POST STUFF TO HASHMAP
                            post.put("URL" , downloadUri.toString());
                            post.put("timestamp" , timeStamp);
                            post.put("uploadedBy" , uploadedBy);
                            post.put("tags" , tagList);
                            reference.child(childRef).setValue(post);

                            addBrands(tagList);

                            uploadPreview.setImageDrawable(getResources().getDrawable(R.drawable.clicktoadd));
                            postTags.setText("");

                        } else {
                            // Handle failures
                            // ...
                            Log.d("FBDB2" , "ERR");
                        }
                    }
                });


            }else{
                Toast.makeText(getContext() , "Add Brands!" , Toast.LENGTH_LONG).show();
            }

        }

    }

    public void addBrands(final ArrayList <String> L){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        Query query = ref.child(mAuth.getUid()).child("brands");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                List<String> updateUserBrands = new ArrayList<String>();

                if (snapshot.exists()) {

                    int length = (int) snapshot.getChildrenCount();
                    for(int i = 0; i < length; i++){
                        String temp = snapshot.child(String.valueOf(i)).getValue().toString();
                        System.out.println(temp);
                        userBrands.add(temp);
                    }

                    L.removeAll(userBrands);

                    System.out.println(L);

                    L.addAll(userBrands);

                    System.out.println(L);

                    //TO GET RID OF WEIRD DUPLICATE STUFF WITH FIREBASE AND ALL THAT
                    ArrayList<String> finalUpload = new ArrayList<String>();

                    for (String element : L) {
                        if (!finalUpload.contains(element)) {
                            finalUpload.add(element);
                        }
                    }

                    HashMap brands = new HashMap();
                    brands.put("brands" , finalUpload);
                    ref.child(mAuth.getUid()).updateChildren(brands);

                }else{
                    HashMap brands = new HashMap();
                    brands.put("brands" , L);
                    ref.child(mAuth.getUid()).updateChildren(brands);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void cropPicture(){
        galleryIntent();
        Log.d("PROFURI" , "WORKING");
    };

    private void galleryIntent() {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(gallery ,  1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK){
            postURI = data.getData();
            CropImage.activity(postURI)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(getContext() , this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                postURI = result.getUri();
                uploadPreview.setImageURI(postURI);
            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception e = result.getError();
            }
        }
    }

}
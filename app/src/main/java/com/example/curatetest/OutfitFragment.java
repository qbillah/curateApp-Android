package com.example.curatetest;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OutfitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OutfitFragment extends Fragment {

    private FirebaseAuth mAuth;

    private GridView postGrid;

    ArrayList<String> postURLs = new ArrayList<>();
    ArrayList<String> postIDs = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OutfitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OutfitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OutfitFragment newInstance(String param1, String param2) {
        OutfitFragment fragment = new OutfitFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_outfit, container, false);

        postGrid = (GridView) v.findViewById(R.id.postGrid);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        Query query = ref.child(mAuth.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String user = snapshot.child("username").getValue(String.class);

                    final Query posts = FirebaseDatabase.getInstance().getReference("posts").orderByChild("uploadedBy").equalTo(user);

                    posts.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot snap : snapshot.getChildren()) {

                                String postID = snap.getKey().toString();
                                String URL = snap.child("URL").getValue().toString();

                                postIDs.add(postID);
                                postURLs.add(URL);

                                Collections.reverse(postIDs);
                                Collections.reverse(postURLs);
                            }

                            System.out.println(postURLs);

                            postGrid.setAdapter(new ImageListAdapter(getContext() , postURLs));

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    ChildEventListener cel = new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            if(snapshot.exists()){
                                for (DataSnapshot snap : snapshot.getChildren()) {

                                    String postID = snap.getKey().toString();
                                    String URL = snap.child("URL").getValue().toString();
                                    String timestamp = snap.child("timestamp").getValue().toString();
                                    String uploadedBy = snap.child("uploadedBy").getValue().toString();

                                    postIDs.add(postID);
                                    postURLs.add(URL);

                                    Collections.reverse(postIDs);
                                    Collections.reverse(postURLs);
                                }

                                System.out.println(postURLs);
                                postGrid.setAdapter(new ImageListAdapter(getContext() , postURLs));
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for (DataSnapshot snap : snapshot.getChildren()) {

                                    String postID = snap.getKey().toString();
                                    String URL = snap.child("URL").getValue().toString();
                                    String timestamp = snap.child("timestamp").getValue().toString();
                                    String uploadedBy = snap.child("uploadedBy").getValue().toString();

                                    postIDs.add(postID);
                                    postURLs.add(URL);

                                    Collections.reverse(postIDs);
                                    Collections.reverse(postURLs);
                                }

                                System.out.println(postURLs);
                                postGrid.setAdapter(new ImageListAdapter(getContext() , postURLs));
                            }
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        postGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent goToPost = new Intent(getActivity() , ViewProfilePost.class);
                goToPost.putExtra("postID" , postIDs.get(i));
                startActivity(goToPost);
            }
        });

        return v;
    }
}
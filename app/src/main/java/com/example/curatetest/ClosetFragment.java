package com.example.curatetest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClosetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClosetFragment extends Fragment {

    private FirebaseAuth mAuth;
    private StorageReference storageRef; // STORAGE REFERENCE - FIREBASE

    private TextView displayUserID , displayUserBio;
    private TabLayout userTabLayout;
    private ViewPager2 userTabDisplay;
    private CircleImageView userPPF;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ClosetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClosetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClosetFragment newInstance(String param1, String param2) {
        ClosetFragment fragment = new ClosetFragment();
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
        storageRef = FirebaseStorage.getInstance().getReference(); // INIT - FIREBASE STORAGE REFERENCE
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_closet, container, false);

        displayUserID = (TextView) view.findViewById(R.id.userID);
        displayUserBio = (TextView) view.findViewById(R.id.userBio);
        userTabLayout = (TabLayout) view.findViewById(R.id.profileTabLayout);
        userTabDisplay = (ViewPager2) view.findViewById(R.id.profileFragView);


        //FRAGMENT IN A FRAGMENT - FRAGMENTCEPTION
        //final ProfileTabAdapter adapter = new ProfileTabAdapter(this , getFragmentManager() , userTabLayout.getTabCount());

        userTabDisplay.setAdapter(new ProfileTabAdapter(this));

        //FRAGMETTING IN THE FRAGMENT
        TabLayoutMediator tabMediator = new TabLayoutMediator(
                userTabLayout, userTabDisplay, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Outfits");
                        break;
                    case 1:
                        tab.setText("Brands");
                        break;
                    case 2:
                        tab.setText("You");
                        break;
                }
            }
        }
        );
        tabMediator.attach();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("users").child(mAuth.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String ppf = dataSnapshot.child("PPF").getValue(String.class);

                    userPPF = (CircleImageView) getView().findViewById(R.id.userPPF);
                    Picasso.get().load(ppf).resize(55, 55).centerCrop().into(userPPF);


                    ChildEventListener cel = new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            if (snapshot.exists()) {
                                String ppf = snapshot.child("PPF").getValue(String.class);

                                userPPF = (CircleImageView) getView().findViewById(R.id.userPPF);
                                Picasso.get().load(ppf).resize(55, 55).centerCrop().into(userPPF);
                            }else{
                                displayUserID.setText("nil");
                            }
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                }else{
                    displayUserID.setText("nil");
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
                displayUserID.setText("nil");
            }
        });

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String user = snapshot.child("username").getValue(String.class);
                    String bio = snapshot.child("bio").getValue(String.class);
                    displayUserID.setText("@" + user);
                    displayUserBio.setText(bio);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

}
package com.example.curatetest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BrandsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrandsFragment extends Fragment {

    private FirebaseAuth mAuth;

    private ListView brandList;

    private ArrayList<String> brands = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BrandsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BrandsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BrandsFragment newInstance(String param1, String param2) {
        BrandsFragment fragment = new BrandsFragment();
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
        View v = inflater.inflate(R.layout.fragment_brands, container, false);
        brandList = (ListView) v.findViewById(R.id.brandDisplay);

        Query posts = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
        posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    int count = (int) snapshot.child("brands").getChildrenCount();
                    if(count == 0){
                        Snackbar.make(getView() , "Nothing to display!" , Snackbar.LENGTH_SHORT)
                                .show();
                    }else{
                        brands.addAll((Collection<? extends String>) snapshot.child("brands").getValue());
                        ArrayAdapter adapter = new ArrayAdapter <String>(getContext() , R.layout.list_item , brands);
                        brandList.setAdapter(adapter);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ChildEventListener cel = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    brands.addAll((Collection<? extends String>) snapshot.child("brands").getValue());
                    ArrayAdapter adapter = new ArrayAdapter <String>(getContext() , R.layout.list_item , brands);
                    brandList.setAdapter(adapter);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                brands.addAll((Collection<? extends String>) snapshot.child("brands").getValue());
                ArrayAdapter adapter = new ArrayAdapter <String>(getContext() , R.layout.list_item , brands);
                brandList.setAdapter(adapter);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        return v;
    }
}
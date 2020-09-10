package com.example.curatetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);

        BottomNavigationView btmNav = findViewById(R.id.bottomNavView);
        btmNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentLayout,new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch(item.getItemId()){
                        case R.id.home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.explore:
                            selectedFragment = new ExploreFragment();
                            break;
                        case R.id.upload:
                            selectedFragment = new UploadFragment();
                            break;
                        case R.id.updates:
                            selectedFragment = new UpdateFragment();
                            break;
                        case R.id.closet:
                            selectedFragment = new ClosetFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentLayout,selectedFragment).commit();
                    return true;
                }
            };
}
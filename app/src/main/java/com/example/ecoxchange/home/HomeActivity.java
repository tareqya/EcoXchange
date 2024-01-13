package com.example.ecoxchange.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.ecoxchange.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomeActivity extends AppCompatActivity {

    private FrameLayout home_FL_home;
    private FrameLayout home_FL_addPost;
    private FrameLayout home_FL_profile;
    private BottomNavigationView home_BN;

    private HomeFragment homeFragment;
    private AddPostFragment addPostFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {

                    }
                });
        FirebaseMessaging.getInstance().subscribeToTopic("REMINDER");
        findViews();
        initVars();
    }

    private void findViews() {
        home_FL_home = findViewById(R.id.home_FL_home);
        home_FL_addPost = findViewById(R.id.home_FL_addPost);
        home_FL_profile = findViewById(R.id.home_FL_profile);
        home_BN = findViewById(R.id.home_BN);
    }

    private void initVars() {
        connectFragments();
        home_FL_home.setVisibility(View.VISIBLE);
        home_FL_addPost.setVisibility(View.INVISIBLE);
        home_FL_profile.setVisibility(View.INVISIBLE);

        home_BN.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.home){
                    // home page
                    home_FL_home.setVisibility(View.VISIBLE);
                    home_FL_addPost.setVisibility(View.INVISIBLE);
                    home_FL_profile.setVisibility(View.INVISIBLE);
                }else if (item.getItemId() == R.id.profile){
                    // profile page
                    home_FL_home.setVisibility(View.INVISIBLE);
                    home_FL_addPost.setVisibility(View.INVISIBLE);
                    home_FL_profile.setVisibility(View.VISIBLE);
                }else {
                    // add post page
                    home_FL_home.setVisibility(View.INVISIBLE);
                    home_FL_addPost.setVisibility(View.VISIBLE);
                    home_FL_profile.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });

        if(!checkPermissions()){
            requestPermissions();
        }else{
            addPostFragment.setPermissions(true);
        }
    }

    private void connectFragments() {
        profileFragment = new ProfileFragment(this);
        homeFragment = new HomeFragment(this);
        addPostFragment = new AddPostFragment(this);

        getSupportFragmentManager().beginTransaction().add(R.id.home_FL_home, homeFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.home_FL_addPost, addPostFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.home_FL_profile, profileFragment).commit();

    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                100
        );
    }
    public  boolean checkPermissions() {
        return (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addPostFragment.setPermissions(true);
            } else {
                // Handle the case when the user denies permission
                requestPermissions();
            }
        }
    }
}
package com.example.ecoxchange.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.ecoxchange.R;
import com.example.ecoxchange.database.User;

public class UpdateAccountActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);

        Intent intent = getIntent();

        user = (User) intent.getSerializableExtra("USER");
        Log.d("aa", "Aaa");
    }
}
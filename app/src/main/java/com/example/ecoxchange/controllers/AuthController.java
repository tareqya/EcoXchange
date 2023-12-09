package com.example.ecoxchange.controllers;

import androidx.annotation.NonNull;

import com.example.ecoxchange.AuthCallBack;
import com.example.ecoxchange.auth.AuthUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthController {
    private FirebaseAuth mAuth;
    private AuthCallBack authCallBack;

    public AuthController(){
        mAuth = FirebaseAuth.getInstance();
    }

    public void setAuthCallBack(AuthCallBack authCallBack){
        this.authCallBack = authCallBack;
    }

    public void createAccount(AuthUser authUser){
        mAuth.createUserWithEmailAndPassword(authUser.getEmail(), authUser.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                authCallBack.onCreateAccountComplete(task);
            }
        });
    }
}

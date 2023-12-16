package com.example.ecoxchange.callback;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public interface AuthCallBack {
    void onCreateAccountComplete(Task<AuthResult> task);
    void onLoginComplete(Task<AuthResult> task);
}

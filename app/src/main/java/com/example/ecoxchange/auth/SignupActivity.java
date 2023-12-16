package com.example.ecoxchange.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ecoxchange.callback.AuthCallBack;
import com.example.ecoxchange.R;
import com.example.ecoxchange.callback.UserCallBack;
import com.example.ecoxchange.controllers.AuthController;
import com.example.ecoxchange.controllers.UserController;
import com.example.ecoxchange.database.User;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;

public class SignupActivity extends AppCompatActivity {
    private TextInputLayout signup_TF_firstName;
    private TextInputLayout signup_TF_lastName;
    private TextInputLayout signup_TF_email;
    private TextInputLayout signup_TF_phone;
    private TextInputLayout signup_TF_password;
    private TextInputLayout signup_TF_confirmPassword;
    private Button signup_BTN_createAccount;
    private ProgressBar signup_PB_loading;
    private AuthController authController;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        findViews();
        initVars();
    }

    private void findViews() {
        signup_TF_firstName = findViewById(R.id.signup_TF_firstName);
        signup_TF_lastName = findViewById(R.id.signup_TF_lastName);
        signup_TF_email = findViewById(R.id.signup_TF_email);
        signup_TF_phone = findViewById(R.id.signup_TF_phone);
        signup_TF_password = findViewById(R.id.signup_TF_password);
        signup_TF_confirmPassword = findViewById(R.id.signup_TF_confirmPassword);
        signup_BTN_createAccount = findViewById(R.id.signup_BTN_createAccount);
        signup_PB_loading = findViewById(R.id.signup_PB_loading);
    }

    private void initVars() {
        authController = new AuthController();
        authController.setAuthCallBack(new AuthCallBack() {
            @Override
            public void onCreateAccountComplete(Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String firstName = signup_TF_firstName.getEditText().getText().toString();
                    String lastName = signup_TF_lastName.getEditText().getText().toString();
                    String email = signup_TF_email.getEditText().getText().toString();
                    String phone = signup_TF_phone.getEditText().getText().toString();
                    String uid = authController.getCurrentUser().getUid();
                    User user = new User()
                            .setEmail(email)
                            .setFirstName(firstName)
                            .setLastName(lastName)
                            .setPhone(phone);
                    user.setKey(uid);

                    userController.saveUser(user);
                }else{
                    String error = task.getException().getMessage().toString();
                    Toast.makeText(SignupActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoginComplete(Task<AuthResult> task) {

            }
        });

        userController = new UserController();
        userController.setUserCallBack(new UserCallBack() {
            @Override
            public void onSaveUserComplete(Task<Void> task) {
                signup_PB_loading.setVisibility(View.INVISIBLE);
                if(task.isSuccessful()){
                    Toast.makeText(SignupActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    authController.logout();
                    finish();
                }else{
                    String error = task.getException().getMessage().toString();
                    Toast.makeText(SignupActivity.this, error, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFetchUserComplete(User user) {

            }
        });

        signup_BTN_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkInputs()){
                    Toast.makeText(SignupActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!checkPassword()){
                    Toast.makeText(SignupActivity.this, "Password must be at least 6 characters and must be match with confirm password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // create account
                String email = signup_TF_email.getEditText().getText().toString();
                String password = signup_TF_password.getEditText().getText().toString();
                AuthUser authUser = new AuthUser(email, password);
                authController.createAccount(authUser);
                signup_PB_loading.setVisibility(View.VISIBLE);
            }
        });
    }

    public boolean checkInputs(){
        String firstName = signup_TF_firstName.getEditText().getText().toString();
        String lastName = signup_TF_lastName.getEditText().getText().toString();
        String email = signup_TF_email.getEditText().getText().toString();
        String phone = signup_TF_phone.getEditText().getText().toString();

        if(firstName.length() == 0 || lastName.length() == 0 || email.length() == 0)
            return  false;
        if(phone.length() < 10)
            return false;

        return true;
    }

    public boolean checkPassword(){
        String confirmPassword = signup_TF_confirmPassword.getEditText().getText().toString();
        String password = signup_TF_password.getEditText().getText().toString();
        if(password.length() < 6)
            return false;
        if(!confirmPassword.equals(password))
            return false;
        return true;
    }
}
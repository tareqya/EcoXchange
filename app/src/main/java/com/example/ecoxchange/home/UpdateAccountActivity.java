package com.example.ecoxchange.home;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ecoxchange.R;
import com.example.ecoxchange.callback.UserCallBack;
import com.example.ecoxchange.controllers.AuthController;
import com.example.ecoxchange.controllers.StorageController;
import com.example.ecoxchange.controllers.UserController;
import com.example.ecoxchange.database.User;
import com.example.ecoxchange.utils.Generic;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateAccountActivity extends AppCompatActivity {

    private User user;
    private CircleImageView updateAccount_IV_image;
    private FloatingActionButton updateAccount_FBTN_uploadImage;
    private TextInputLayout updateAccount_TF_firstName;
    private TextInputLayout updateAccount_TF_lastName;
    private TextInputLayout updateAccount_TF_phone;
    private Button updateAccount_BTN_createAccount;
    private Uri selectedImageUri;
    private AuthController authController;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);

        Intent intent = getIntent();

        user = (User) intent.getSerializableExtra("USER");
        findViews();
        initVars();
        displayUser();
    }

    private void displayUser() {
        updateAccount_TF_firstName.getEditText().setText(user.getFirstName());
        updateAccount_TF_lastName.getEditText().setText(user.getLastName());
        updateAccount_TF_phone.getEditText().setText(user.getPhone());

        if(user.getImageUrl() != null){
            // set the image
            Glide
                    .with(this)
                    .load(user.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.profile)
                    .into(updateAccount_IV_image);
        }
    }

    private void initVars() {
        authController = new AuthController();
        userController = new UserController();

        userController.setUserCallBack(new UserCallBack() {
            @Override
            public void onSaveUserComplete(Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(UpdateAccountActivity.this, "User account updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    String err = task.getException().getMessage().toString();
                    Toast.makeText(UpdateAccountActivity.this, err, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFetchUserComplete(User user) {

            }
        });
        updateAccount_BTN_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser();
            }
        });

        updateAccount_FBTN_uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageSourceDialog();
            }
        });
    }

    private void updateUser() {
        user.setPhone(updateAccount_TF_phone.getEditText().getText().toString());
        user.setFirstName(updateAccount_TF_firstName.getEditText().getText().toString());
        user.setLastName(updateAccount_TF_lastName.getEditText().getText().toString());
        String uid = authController.getCurrentUser().getUid();
        user.setKey(uid);
        if(selectedImageUri != null){
            String ext = Generic.getFileExtension(this, selectedImageUri);
            String imagePath = "profileImages/"+uid+"."+ext;
            user.setImagePath(imagePath);
            StorageController storageController = new StorageController();
            if(storageController.uploadImage(selectedImageUri, imagePath)){
                userController.saveUser(user);
            }
        }else{
            userController.saveUser(user);
        }

    }

    private void findViews() {
        updateAccount_IV_image = findViewById(R.id.updateAccount_IV_image);
        updateAccount_TF_firstName = findViewById(R.id.updateAccount_TF_firstName);
        updateAccount_TF_lastName = findViewById(R.id.updateAccount_TF_lastName);
        updateAccount_TF_phone = findViewById(R.id.updateAccount_TF_phone);
        updateAccount_BTN_createAccount = findViewById(R.id.updateAccount_BTN_createAccount);
        updateAccount_FBTN_uploadImage = findViewById(R.id.updateAccount_FBTN_uploadImage);
    }

    private void showImageSourceDialog() {
        CharSequence[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case 0:
                        openCamera();
                        break;
                    case 1:
                        openGallery();
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraResults.launch(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery_results.launch(intent);
    }

    private final ActivityResultLauncher<Intent> gallery_results = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            try {
                                Intent intent = result.getData();
                                selectedImageUri = intent.getData();
                                final InputStream imageStream = UpdateAccountActivity.this.getContentResolver().openInputStream(selectedImageUri);
                                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                updateAccount_IV_image.setImageBitmap(selectedImage);
                            }
                            catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Toast.makeText(UpdateAccountActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(UpdateAccountActivity.this, "Gallery canceled", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });

    private final ActivityResultLauncher<Intent> cameraResults = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            Intent intent = result.getData();
                            Bitmap bitmap = (Bitmap)  intent.getExtras().get("data");
                            updateAccount_IV_image.setImageBitmap(bitmap);
                            selectedImageUri = Generic.getImageUri(UpdateAccountActivity.this, bitmap);
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(UpdateAccountActivity.this, "Camera canceled", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
}
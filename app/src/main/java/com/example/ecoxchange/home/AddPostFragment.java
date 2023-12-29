package com.example.ecoxchange.home;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ecoxchange.R;
import com.example.ecoxchange.callback.PostCallBack;
import com.example.ecoxchange.controllers.AuthController;
import com.example.ecoxchange.controllers.PostController;
import com.example.ecoxchange.controllers.StorageController;
import com.example.ecoxchange.database.Post;
import com.example.ecoxchange.utils.Generic;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;


public class AddPostFragment extends Fragment {
    public static final String POSTS_IMAGES = "Posts";

    private Activity activity;
    private TextInputLayout fAddPost_TF_title;
    private TextInputLayout fAddPost_TF_description;
    private ImageView fAddPost_IV_photo;
    private Button fAddPost_BTN_addPost;
    private ProgressBar fAddPost_PB_loading;
    private Uri selectedImageUri;
    private boolean permissions;
    private PostController postController;

    public AddPostFragment(Activity activity) {
        this.activity = activity;
        this.permissions = false;
        postController = new PostController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);
        findViews(view);
        initVars();
        return view;
    }

    private void initVars() {
        postController.setPostCallBack(new PostCallBack() {
            @Override
            public void onPostSaveComplete(Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(activity, "Post uploaded successfully", Toast.LENGTH_SHORT).show();
                    fAddPost_TF_title.getEditText().setText("");
                    fAddPost_TF_description.getEditText().setText("");
                    selectedImageUri = null;
                    fAddPost_IV_photo.setImageResource(R.drawable.baseline_add_photo_alternate_24);

                }else{
                    String err = task.getException().getMessage().toString();
                    Toast.makeText(activity, err, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPostsFetchComplete(ArrayList<Post> posts) {

            }
        });

        fAddPost_BTN_addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // upload new post
                if(!checkInput()) return;

                AuthController authController = new AuthController();
                String uid =  authController.getCurrentUser().getUid();
                String title = fAddPost_TF_title.getEditText().getText().toString();
                String description = fAddPost_TF_description.getEditText().getText().toString();

                String ext = Generic.getFileExtension(activity, selectedImageUri);
                String imagePath = POSTS_IMAGES + "/" + Generic.generateRandomString(10) + "." + ext;

                StorageController storageController = new StorageController();
                if(storageController.uploadImage(selectedImageUri, imagePath)){
                    Post p = new Post()
                            .setUserId(uid)
                            .setDescription(description)
                            .setTitle(title)
                            .setImagePath(imagePath)
                            .setPhone(ProfileFragment.currentUser.getPhone());
                    postController.savePost(p);
                }
            }
        });

        fAddPost_IV_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(permissions){
                    showImageSourceDialog();
                }
            }
        });
    }

    private boolean checkInput() {
        String title = fAddPost_TF_title.getEditText().getText().toString();
        String description = fAddPost_TF_description.getEditText().getText().toString();
        if(title.isEmpty()){
            Toast.makeText(activity, "Title is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(description.isEmpty()){
            Toast.makeText(activity, "Description is required!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(selectedImageUri == null){
            Toast.makeText(activity, "Image is required!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void findViews(View view) {
        fAddPost_TF_title = view.findViewById(R.id.fAddPost_TF_title);
        fAddPost_TF_description = view.findViewById(R.id.fAddPost_TF_description);
        fAddPost_IV_photo = view.findViewById(R.id.fAddPost_IV_photo);
        fAddPost_BTN_addPost = view.findViewById(R.id.fAddPost_BTN_addPost);
        fAddPost_PB_loading = view.findViewById(R.id.fAddPost_PB_loading);

    }

    public void setPermissions(boolean permissions) {
        this.permissions = permissions;
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraResults.launch(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery_results.launch(intent);
    }

    private void showImageSourceDialog() {
        CharSequence[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
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

    private final ActivityResultLauncher<Intent> gallery_results = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            try {
                                Intent intent = result.getData();
                                selectedImageUri = intent.getData();
                                final InputStream imageStream = activity.getContentResolver().openInputStream(selectedImageUri);
                                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                fAddPost_IV_photo.setImageBitmap(selectedImage);
                            }
                            catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show();
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(activity, "Gallery canceled", Toast.LENGTH_SHORT).show();
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
                            fAddPost_IV_photo.setImageBitmap(bitmap);
                            selectedImageUri = Generic.getImageUri(activity, bitmap);
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(activity, "Camera canceled", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });

}
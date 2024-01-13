package com.example.ecoxchange.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.example.ecoxchange.R;
import com.example.ecoxchange.adapters.PostAdapter;
import com.example.ecoxchange.callback.PostCallBack;
import com.example.ecoxchange.callback.PostListener;
import com.example.ecoxchange.controllers.AuthController;
import com.example.ecoxchange.controllers.PostController;
import com.example.ecoxchange.database.Post;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MyPostsActivity extends AppCompatActivity {

    private PostController postController;
    private AuthController authController;
    private RecyclerView myPosts_RV_posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        findViews();
        initVars();
    }

    private void initVars() {
        authController = new AuthController();
        postController = new PostController();
        postController.setPostCallBack(new PostCallBack() {
            @Override
            public void onPostSaveComplete(Task<Void> task) {

            }

            @Override
            public void onPostsFetchComplete(ArrayList<Post> posts) {
                PostAdapter postAdapter = new PostAdapter(MyPostsActivity.this, posts);
                postAdapter.setPostListener(new PostListener() {
                    @Override
                    public void onClick(Post post, int position) {
                        Toast.makeText(MyPostsActivity.this, post.getTitle(), Toast.LENGTH_SHORT).show();
                        removeDialog(post, position);
                    }
                });
                myPosts_RV_posts.setLayoutManager(new LinearLayoutManager(MyPostsActivity.this, LinearLayoutManager.VERTICAL, false));
                myPosts_RV_posts.setHasFixedSize(true);
                myPosts_RV_posts.setItemAnimator(new DefaultItemAnimator());
                myPosts_RV_posts.setAdapter(postAdapter);
            }
        });

        String uid = authController.getCurrentUser().getUid();
        postController.fetchUserPosts(uid);
    }

    private void findViews() {
        myPosts_RV_posts = findViewById(R.id.myPosts_RV_posts);
    }

    public void removeDialog(Post post, int position){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Remove Post");
        alertDialogBuilder.setMessage("Are you sure that you want to remove this post ?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postController.removePost(post);
                dialog.dismiss();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
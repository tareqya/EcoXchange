package com.example.ecoxchange.controllers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ecoxchange.callback.PostCallBack;
import com.example.ecoxchange.database.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PostController {
    public static final String POSTS_TABLE = "Posts";
    private FirebaseFirestore db ;
    private PostCallBack postCallBack;

    public PostController() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void setPostCallBack(PostCallBack postCallBack) {
        this.postCallBack = postCallBack;
    }

    public void savePost(Post post){
        db.collection(POSTS_TABLE).document().set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                postCallBack.onPostSaveComplete(task);
            }
        });
    }

    public void fetchPosts(){
        db.collection(POSTS_TABLE)
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                ArrayList<Post> posts = new ArrayList<>();
                if(value != null){
                    StorageController storageController = new StorageController();
                    for(DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        Post post =  documentSnapshot.toObject(Post.class);
                        String imageUrl = storageController.getDownloadUrl(post.getImagePath());
                        post.setImageUrl(imageUrl);
                        posts.add(post);
                    }
                }
                postCallBack.onPostsFetchComplete(posts);
            }
        });
    }

    public void fetchUserPosts(String uid){

    }
}

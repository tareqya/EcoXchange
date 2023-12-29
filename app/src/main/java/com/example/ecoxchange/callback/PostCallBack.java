package com.example.ecoxchange.callback;

import com.example.ecoxchange.database.Post;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public interface PostCallBack {
    void onPostSaveComplete(Task<Void> task);
    void onPostsFetchComplete(ArrayList<Post> posts);
}

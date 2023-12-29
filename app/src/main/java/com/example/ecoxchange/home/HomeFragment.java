package com.example.ecoxchange.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ecoxchange.R;
import com.example.ecoxchange.adapters.PostAdapter;
import com.example.ecoxchange.callback.PostCallBack;
import com.example.ecoxchange.controllers.PostController;
import com.example.ecoxchange.database.Post;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private Activity activity;
    private TextInputLayout fHome_TF_search;
    private RecyclerView fHome_RV_posts;
    private PostController postController;
    private ArrayList<Post> posts;
    private ArrayList<Post> allPosts;
    private PostAdapter postAdapter;

    public HomeFragment(Activity activity) {
        this.activity = activity;
        postController = new PostController();
        posts = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        findViews(view);
        initVars();
        return view;
    }

    private void findViews(View view) {
        fHome_TF_search = view.findViewById(R.id.fHome_TF_search);
        fHome_RV_posts = view.findViewById(R.id.fHome_RV_posts);
    }

    private void initVars() {
        postController.setPostCallBack(new PostCallBack() {
            @Override
            public void onPostSaveComplete(Task<Void> task) {

            }
            @Override
            public void onPostsFetchComplete(ArrayList<Post> fetchedPosts) {
                allPosts = fetchedPosts;
                postAdapter = new PostAdapter(activity, allPosts);

                fHome_RV_posts.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                fHome_RV_posts.setHasFixedSize(true);
                fHome_RV_posts.setItemAnimator(new DefaultItemAnimator());
                fHome_RV_posts.setAdapter(postAdapter);
            }
        });

        postController.fetchPosts();


        fHome_TF_search.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().isEmpty()){
                    postAdapter.setPosts(allPosts);
                }else{
                    // filter posts
                    posts.clear();
                    for(Post post: allPosts){
                        if(post.getTitle().startsWith(charSequence.toString())){
                            posts.add(post);
                        }
                    }
                    postAdapter.setPosts(posts);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
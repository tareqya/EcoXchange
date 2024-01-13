package com.example.ecoxchange.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecoxchange.R;
import com.example.ecoxchange.callback.PostListener;
import com.example.ecoxchange.database.Post;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Activity activity;
    private ArrayList<Post> posts = new ArrayList<>();
    private PostListener postListener;
    public PostAdapter(Activity activity, ArrayList<Post> posts){
        this.posts = posts;
        this.activity = activity;
    }

    public void setPostListener(PostListener postListener){
        this.postListener = postListener;
    }
    public void setPosts(ArrayList<Post> posts){
        this.posts = posts;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PostViewHolder postViewHolder = (PostViewHolder) holder;
        Post post = getItem(position);
        postViewHolder.post_TV_description.setText(post.getDescription());
        postViewHolder.post_TV_title.setText(post.getTitle());
        Glide
            .with(activity)
            .load(post.getImageUrl())
            .centerCrop()
            .into( postViewHolder.post_IV_image);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
    private Post getItem(int position) {
        return posts.get(position);
    }

    public class PostViewHolder extends  RecyclerView.ViewHolder {

        public ImageView post_IV_image;
        public TextView post_TV_title;
        public TextView post_TV_description;
        public Button post_BTN_call;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            post_BTN_call = itemView.findViewById(R.id.post_BTN_call);
            post_IV_image = itemView.findViewById(R.id.post_IV_image);
            post_TV_description = itemView.findViewById(R.id.post_TV_description);
            post_TV_title = itemView.findViewById(R.id.post_TV_title);

            post_BTN_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Post post = getItem(position);
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    // Set the data (phone number) for the Intent
                    intent.setData(Uri.parse("tel:" + post.getPhone()));
                    activity.startActivity(intent);

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = getLayoutPosition();
                    Post post = posts.get(i);
                    if(postListener != null){
                        postListener.onClick(post, i);
                    }
                }
            });
        }

    }
}

package com.example.ecoxchange.callback;

import com.example.ecoxchange.database.Post;

public interface PostListener {
    void onClick(Post post, int position);
}

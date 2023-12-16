package com.example.ecoxchange.callback;

import com.example.ecoxchange.database.User;
import com.google.android.gms.tasks.Task;

public interface UserCallBack {
    void onSaveUserComplete(Task<Void> task);
    void onFetchUserComplete(User user);
}

package com.example.ecoxchange.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ecoxchange.R;
import com.example.ecoxchange.auth.LoginActivity;
import com.example.ecoxchange.callback.UserCallBack;
import com.example.ecoxchange.controllers.AuthController;
import com.example.ecoxchange.controllers.UserController;
import com.example.ecoxchange.database.User;
import com.google.android.gms.tasks.Task;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private Activity activity;
    private CircleImageView fProfile_IV_profileImage;
    private TextView fProfile_TV_name;
    private CardView fProfile_CV_editDetails;
    private CardView fProfile_CV_posts;
    private CardView fProfile_CV_logout;
    private AuthController authController;
    private UserController userController;
    public static User currentUser;

    public ProfileFragment(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        findViews(view);
        initVars();
        return view;
    }

    private void initVars() {
        authController = new AuthController();
        userController = new UserController();
        userController.setUserCallBack(new UserCallBack() {
            @Override
            public void onSaveUserComplete(Task<Void> task) {

            }

            @Override
            public void onFetchUserComplete(User user) {
                fProfile_TV_name.setText(user.getFullName());
                if(user.getImageUrl() != null){
                    // set the image
                    Glide
                            .with(activity)
                            .load(user.getImageUrl())
                            .centerCrop()
                            .placeholder(R.drawable.profile)
                            .into(fProfile_IV_profileImage);
                }
                currentUser = user;
            }
        });
        fProfile_CV_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, LoginActivity.class);
                startActivity(intent);
                activity.finish();
                authController.logout();
            }
        });

        fProfile_CV_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fProfile_CV_editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, UpdateAccountActivity.class);
                intent.putExtra("USER", currentUser);
                startActivity(intent);
            }
        });
        String uid = authController.getCurrentUser().getUid();
        userController.fetchUserData(uid);
    }

    private void findViews(View view) {
        fProfile_IV_profileImage = view.findViewById(R.id.fProfile_IV_profileImage);
        fProfile_TV_name = view.findViewById(R.id.fProfile_TV_name);
        fProfile_CV_editDetails = view.findViewById(R.id.fProfile_CV_editDetails);
        fProfile_CV_posts = view.findViewById(R.id.fProfile_CV_posts);
        fProfile_CV_logout = view.findViewById(R.id.fProfile_CV_logout);
    }

}
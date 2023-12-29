package com.example.ecoxchange.controllers;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

public class StorageController {
    private FirebaseStorage storage ;
    public StorageController(){
       this.storage = FirebaseStorage.getInstance();
    }

    public String getDownloadUrl(String path){
        Task<Uri> task = this.storage.getReference().child(path).getDownloadUrl();
        while (!task.isComplete() && !task.isCanceled());
        return task.getResult().toString();
    }

    public boolean uploadImage(Uri imageUri, String imagePath){
        try{
            UploadTask uploadTask = storage.getReference(imagePath).putFile(imageUri);
            while (!uploadTask.isComplete() && !uploadTask.isCanceled());
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage().toString());
            return false;
        }

    }

}

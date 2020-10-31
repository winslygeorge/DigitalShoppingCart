package com.deepdiver.digitalshoppingcart;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class SendImageToFireBase  extends AsyncTask<String, ProgressBar, String> {

    public Uri file;
    public Context context;
   public String savedImgName =  "";
    private StorageReference mStorageRef;
    public SendImageToFireBase(Context context, Uri uri){
        this.file = uri;
        this.context = context;
    }

    public String uniqueName;

    @Override
    protected String doInBackground(String... strings) {

        uniqueName = strings[0];
        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference riversRef = mStorageRef.child("gs://digitalcartstore.appspot.com/"+ uniqueName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getUploadSessionUri();

                        savedImgName = uniqueName;
                        Toast.makeText(context, downloadUrl.getPath(), Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads

                        Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();

                        // ...
                    }
                });
        return savedImgName;
    }
}

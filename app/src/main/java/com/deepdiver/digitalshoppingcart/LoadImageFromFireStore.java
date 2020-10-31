package com.deepdiver.digitalshoppingcart;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class LoadImageFromFireStore extends AsyncTask<String , ProgressBar, Boolean> {

    public Uri file;
    public View iview;
    public Context context;
    private StorageReference mStorageRef;
    public LoadImageFromFireStore(Context context, Uri uri, View view){
        this.file = uri;
        this.context = context;
        this.iview  = view;
    }



    @Override
    protected Boolean doInBackground(String... strings) {

        mStorageRef = FirebaseStorage.getInstance().getReference();

        String uniqueName = strings[0];

                File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpeg");

            StorageReference riversRef = mStorageRef.child("gs:/digitalcartstore.appspot.com/"+ uniqueName);

            riversRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file

                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    Picasso.with(context).load(task.getResult()).into((ImageView) iview);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download

                    Log.e("onFailure", exception.getMessage() );
                    Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();

                    // ...
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}

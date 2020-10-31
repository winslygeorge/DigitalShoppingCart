package com.deepdiver.digitalshoppingcart;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.deepdiver.digitalshoppingcart.data.database.DatabaseManager;
import com.deepdiver.digitalshoppingcart.data.database.URLConnector;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class AddProduct extends AppCompatActivity {

    private EditText pro_name_edit;
    private EditText pro_desc_edit;
    private EditText pro_price_edit;
    private EditText pro_serial_edit;
    private EditText pro_imageUrl_edit;
    private Button submit_button;
    protected Button fileBtn;
    private Uri imgUrl;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        pro_name_edit = findViewById(R.id.item_name_view);
        pro_desc_edit = findViewById(R.id.item_desc_editor);
        pro_price_edit = findViewById(R.id.item_price_editor);
        pro_serial_edit = findViewById(R.id.item_serial_editor);
        submit_button = findViewById(R.id.add_pro_btn);

        progressBar = findViewById(R.id.addproduct_progress_bar);

        fileBtn  = findViewById(R.id.fbtn);

        fileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent selectImageIntent  = new Intent();
                selectImageIntent.setAction(Intent.ACTION_GET_CONTENT);

                selectImageIntent.setType("image/*");

                startActivityForResult(selectImageIntent, 1);
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String product_name = pro_name_edit.getText().toString();
                String product_desc = pro_desc_edit.getText().toString();
                String product_serial = pro_serial_edit.getText().toString();

                String product_price = pro_price_edit.getText().toString();

                String [] product_details = {product_name, product_serial, product_desc, product_price, UUID.randomUUID().toString()};


                try {
                    if(  new AddProductAsync(imgUrl).execute(product_details).get()){

                        new SendImageToFireBase(getApplicationContext(), imgUrl).execute(product_details[4]);

                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public class AddProductAsync extends AsyncTask<String , ProgressBar, Boolean>{

        private URLConnector db;

        private Uri fileUri;

        public AddProductAsync(Uri fileUri){
            this.fileUri  = fileUri;
        }
        @Override
        protected Boolean doInBackground(String... strings) {

            progressBar.setVisibility(View.VISIBLE);
            String product_name = strings[0];
            String product_desc = strings[2];
            String product_serial = strings[1];
            String product_imageUrl = strings[4];
            float product_price = Float.parseFloat(strings[3]);

            db = new URLConnector("https://georgosdigital.azurewebsites.net/digitalurlget");
            try {

                ContentValues cv = new ContentValues();


             String [] fields = {DatabaseManager.product_name,
                DatabaseManager.desc,
                DatabaseManager.serial_no,
                DatabaseManager.product_image,
                DatabaseManager.price
                };

                String [] vals = {

                        product_name,
                        product_desc,
                        product_serial,
                        product_imageUrl,
                        String.valueOf(product_price)
                };


                       if(Integer.parseInt(String.valueOf(db.handlePostRequest("https://georgosdigital.azurewebsites.net/digitalurlpost", fields, vals, "insert", DatabaseManager.PRODUCT_TABLE, null, null ).get("resultCode"))) == 200 ) {
                           return true;
                       }else{
                           return false;
                       }
            }catch(Exception e){

                Log.e("addingProductToDatabase", "doInBackground: err", e );

                return  false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            progressBar.setVisibility(View.INVISIBLE);

            if(aBoolean == true){



                Toast.makeText(getApplicationContext(), "product was added successfully", Toast.LENGTH_LONG).show();
            }else{

                Toast.makeText(getApplicationContext(), "Operation adding product was unsuccessfully", Toast.LENGTH_LONG).show();

            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode  == 1 && resultCode  == RESULT_OK ){

            Uri fileUri = data.getData();
            Log.i("logurl", "Uri: " + fileUri);

            String filePath = null;
            try {
                this.imgUrl = fileUri;
              //  new LoadImageFromFireStore(getApplicationContext(), fileUri).execute();

            } catch (Exception e) {
                Log.e("logerr","Error: " + e);
                Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
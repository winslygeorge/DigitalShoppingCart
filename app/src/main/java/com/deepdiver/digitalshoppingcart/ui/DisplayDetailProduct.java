package com.deepdiver.digitalshoppingcart.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.deepdiver.digitalshoppingcart.LoadImageFromFireStore;
import com.deepdiver.digitalshoppingcart.R;
import com.deepdiver.digitalshoppingcart.data.Product;
import com.deepdiver.digitalshoppingcart.data.database.DatabaseManager;
import com.deepdiver.digitalshoppingcart.data.database.URLConnector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DisplayDetailProduct extends AppCompatActivity {

    private TextView p_name_view;
    private TextView p_desc_view;
    private TextView price_view;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deatailed_product_layout);

        p_name_view = findViewById(R.id.product_name_txt);
        p_desc_view = findViewById(R.id.product_desc_view);
        price_view = findViewById(R.id.product_price_pricy_view);
        imageView = findViewById(R.id.product_image_view);

       // ImageButton addbtn = findViewById(R.id.b)
    }

    @Override
    protected void onResume() {
        super.onResume();

        int product_id = getIntent().getExtras().getInt("product_id");
        new GetProductAync().execute(product_id);
    }

    public class GetProductAync extends AsyncTask<Integer , ProgressBar, Product>{

        private String product_name;
        private String product_serial_no;
        private String product_desc;
        private float product_price;
        private String product_image_url;
        private Product product;
        private URLConnector db;

        @Override
        protected Product doInBackground(Integer... integers) {
            int p_id= integers[0];

            db = new URLConnector("https://georgosdigital.azurewebsites.net/digitalurlget");

            try {
                String[] cols = {DatabaseManager._ID, DatabaseManager.product_name, DatabaseManager.serial_no, DatabaseManager.desc, DatabaseManager.price, DatabaseManager.product_image};
                String[] s_tring = {DatabaseManager._ID};
                String[] s_args = {String.valueOf(p_id)};
                JSONObject cursor = db.get(DatabaseManager.PRODUCT_TABLE, cols, s_tring, s_args);
                if ((int)cursor.get("resultCode") == 200) {

                    JSONArray jsonArray = (JSONArray) cursor.get("results");
                    int x = 0;
                    while (x < jsonArray.size()) {

                        JSONObject jobj = (JSONObject) jsonArray.get(x);
                        product_name = jobj.get(DatabaseManager.product_name).toString();
                        product_serial_no = jobj.get((DatabaseManager.serial_no)).toString();
                        product_desc = jobj.get((DatabaseManager.desc)).toString();
                        product_price = Float.parseFloat(String.valueOf(jobj.get((DatabaseManager.price))));

                        product_image_url = jobj.get((DatabaseManager.product_image)).toString();
                        x++;
                    }

                    String file = product_image_url;

                    if (file != null) {
                        product = new Product(product_name, file, product_price, product_desc, product_serial_no, -1);
                    } else {

                        product = new Product(product_name, null, product_price, product_desc, product_serial_no, -1);

                    }

                    return product;
                }
                }catch(Exception e){

                    Log.e("onloadproduct", "doInBackground: on load product ", e);
                }
            finally{
                    return product;

            }
        }

        @Override
        protected void onPostExecute(Product product) {
            super.onPostExecute(product);

            if(product != null ){

                p_name_view.setText(product.productName);
                p_desc_view.setText(product.productDesc);
                String price_string = "Ksh. "+ String.valueOf(product_price);
                price_view.setText(price_string);

                if(product.productImageUrl != null){

                  new LoadImageFromFireStore(getApplicationContext(), null, imageView).execute(product.productImageUrl);
                }

            }
        }
    }
}
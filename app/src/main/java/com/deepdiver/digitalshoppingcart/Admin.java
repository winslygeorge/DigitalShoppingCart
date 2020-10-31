package com.deepdiver.digitalshoppingcart;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.deepdiver.digitalshoppingcart.data.Product;
import com.deepdiver.digitalshoppingcart.data.ProductUpdator;
import com.deepdiver.digitalshoppingcart.data.database.DatabaseManager;
import com.deepdiver.digitalshoppingcart.data.database.URLConnector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Admin extends AppCompatActivity {

    private Product product;
    private List<Product> cartProducts = new ArrayList<>();
    private TableLayout cart_table;
    protected  ProgressBar pbadmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cart_table = findViewById(R.id.product_table);

        pbadmin  = findViewById(R.id.pbadmin);



                FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add new product to the database", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                startActivity(new Intent(Admin.this, AddProduct.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new UpdateProductAsync().execute();
    }

    public  class UpdateProductAsync extends AsyncTask<String, ProgressBar, List<Product>> {

        private TableRow newRow;
        private TextView p_name;
        private TextView s_no;
        private TextView price_view;
        private TextView q_view;
       // private TextView total_view;
        private ImageButton d_view;
        private ImageButton add_quantity;
        private ImageButton reduce_quantity;
        public TextView product_id_view;

        @Override
        protected List<Product> doInBackground(String... strings) {

            pbadmin.setVisibility(View.VISIBLE);
            URLConnector db = new URLConnector("https://georgosdigital.azurewebsites.net/digitalurlget");


                    String [] selCols = {
                            DatabaseManager._ID,
                            DatabaseManager.product_name,
                            DatabaseManager.serial_no,
                            DatabaseManager.price
                    };
                    JSONObject dbcursor =  db.get(DatabaseManager.PRODUCT_TABLE, selCols, null, null);

            try {
                JSONArray jsonArray = (JSONArray)dbcursor.get("results");
                if(jsonArray.size()!= 0 ) {


                    int x = 0;
                    while (x < jsonArray.size()) {

                        JSONObject pobj = (JSONObject) jsonArray.get(x);
                        String item_name = pobj.get(DatabaseManager.product_name).toString();
                        String item_serial = pobj.get(DatabaseManager.serial_no).toString();
                        float item_price = Float.parseFloat(String.valueOf(pobj.get(DatabaseManager.price)));
                        int item_id = Integer.parseInt(String.valueOf(pobj.get(DatabaseManager._ID)));

                        product = new Product(item_name, null, item_price, null, item_serial, item_id);

                        if (product != null) {


                            cartProducts.add(product);

                        }
                        x = x + 1;
                    }


                    //}
                    //  }

                    return cartProducts;
                }else{
                    return cartProducts;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Product> products) {
            super.onPostExecute(products);
pbadmin.setVisibility(View.INVISIBLE);
            if(!products.isEmpty()){
                int cart_product_position = 0;
                for (cart_product_position = 0; cart_product_position < products.size(); cart_product_position++){
                    Product cart_product = products.get(cart_product_position);
                    newRow = new TableRow(getApplicationContext() );
                    p_name = new TextView(getApplicationContext());
                    s_no = new TextView(getApplicationContext());
                    price_view = new TextView(getApplicationContext());
                    q_view = new TextView(getApplicationContext());
                  //  total_view = new TextView(getApplicationContext());
                    d_view = new ImageButton(getApplicationContext());
                    add_quantity = new ImageButton(getApplicationContext());
                    reduce_quantity = new ImageButton(getApplicationContext());

                    product_id_view = new TextView(getApplicationContext());


                    d_view.setImageResource(android.R.drawable.ic_menu_delete);
                    add_quantity.setImageResource(android.R.drawable.arrow_up_float);
                    reduce_quantity.setImageResource(android.R.drawable.arrow_down_float);
                    p_name.setText(cart_product.productName);
                    s_no.setText(cart_product.productSerial);
                    price_view.setText(String.valueOf(cart_product.productPrice));
                    price_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    q_view.setText("1");
                    q_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    product_id_view.setText(String.valueOf(cart_product.product_id));
                    product_id_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    //total_view.setText(String.valueOf(cart_product.productPrice));

                    newRow.addView(product_id_view);
                    newRow.addView(p_name);
                    newRow.addView(s_no);
                    newRow.addView(price_view);
                    newRow.addView(q_view);
                    //newRow.addView(total_view);
                    newRow.addView(add_quantity);
                    newRow.addView(reduce_quantity);
                    newRow.addView(d_view);

                    if((cart_product_position%2) == 0 ) {

                        newRow.setBackgroundColor(getResources().getColor(R.color.light_gray));

                    }
                    cart_table.addView(newRow);

                }
                Log.i("oncountrows", "onCreateView: tablerows counts :  "+ cart_table.getChildCount());

                ProductUpdator productUpdator = new ProductUpdator(cart_table, getApplicationContext());
                productUpdator.setOnClickListener();

            }
        }
    }


}

package com.deepdiver.digitalshoppingcart.ui.gallery;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.deepdiver.digitalshoppingcart.PayMentProcess;
import com.deepdiver.digitalshoppingcart.R;
import com.deepdiver.digitalshoppingcart.data.Product;
import com.deepdiver.digitalshoppingcart.data.database.DatabaseManager;
import com.deepdiver.digitalshoppingcart.data.database.URLConnector;
import com.deepdiver.digitalshoppingcart.data.model.CartListAdapter;
import com.deepdiver.digitalshoppingcart.data.model.SessionManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {


    private GalleryViewModel galleryViewModel;
    private Product product;
    private List<Product> cartProducts = new ArrayList<>();
    private TableLayout cart_table;
    private TextView product_id_view;
    private ProgressBar pbbarCart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        pbbarCart = root.findViewById(R.id.pbbarcart);

                cart_table = root.findViewById(R.id.product_table);


        Button pay_btn = root.findViewById(R.id.pay_btn);

        pay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CardView cardView = root.findViewById(R.id.pay_card_selection);
                cardView.setVisibility(View.VISIBLE);

                RadioGroup radioGroup = root.findViewById(R.id.payment_group);

                //int  r_id = radioGroup.getCheckedRadioButtonId();

                radioGroup.check(R.id.mpesa_radioButton);

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if(group.getCheckedRadioButtonId() == R.id.mpesa_radioButton){

                            Toast.makeText(getContext(), "Payment paid by M-pesa", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), PayMentProcess.class);
                            int c_id = new SessionManager(getContext()).sessionPref.getInt("customer_id", -1);
                            intent.putExtra("custId", c_id);

                            startActivity(intent);

                        }else if(group.getCheckedRadioButtonId()== R.id.debit_radioButton3){

                            Toast.makeText(getContext(), "Payment paid by Debit card", Toast.LENGTH_SHORT).show();

                        }else if(group.getCheckedRadioButtonId() == R.id.visa_radioButton2){

                            Toast.makeText(getContext(), "Payment paid by Visa card", Toast.LENGTH_SHORT).show();

                        }
                    }
                });



            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();


        new UpdateCartAsync().execute();
    }

    public  class UpdateCartAsync extends AsyncTask<String, ProgressBar, List<Product>>{

        private TableRow newRow;
        private TextView p_name;
        private TextView s_no;
        private TextView price_view;
        private TextView q_view;
        private TextView total_view;
        private ImageButton d_view;
        private ImageButton add_quantity;
        private ImageButton reduce_quantity;

        @Override
        protected List<Product> doInBackground(String... strings) {

            pbbarCart.setVisibility(View.VISIBLE);

            URLConnector db = new URLConnector("https://georgosdigital.azurewebsites.net/digitalurlget");
            String [] cartCols = {DatabaseManager._ID, DatabaseManager.product_id_foreign, DatabaseManager.customer_id_foreign};
            String[] selsectionString ={ DatabaseManager.customer_id_foreign };

          int c_id = new SessionManager(getContext()).sessionPref.getInt("customer_id", -1);
          String [] selArgs = {String.valueOf(c_id)};
          if(c_id != -1) {
              JSONObject cursor = db.get(DatabaseManager.CART_TABLE, cartCols, selsectionString, selArgs);

              try {
                  JSONArray arr = (JSONArray)cursor.get("results");
                  if (Integer.parseInt(String.valueOf(cursor.get("resultCode"))) == 404) {

                      return null;
                  } else {

                      JSONArray jsonArray = (JSONArray) cursor.get("results");

                      int x = 0;
                      while (x < jsonArray.size()) {

                          JSONObject pobj = (JSONObject) jsonArray.get(x);


                          int p_id = Integer.parseInt(String.valueOf(pobj.get(DatabaseManager.product_id_foreign)));

                          String[] selCols = {
                                  DatabaseManager._ID,
                                  DatabaseManager.product_name,
                                  DatabaseManager.serial_no,
                                  DatabaseManager.price
                          };
                          String[] stringselections ={ DatabaseManager._ID };
                          String[] args = {String.valueOf(p_id)};
                          JSONObject dbcursor = db.get(DatabaseManager.PRODUCT_TABLE, selCols, stringselections, args);

                          JSONArray parr = (JSONArray) dbcursor.get("results");
                          int y = 0;
                          while (y < parr.size()) {

                              JSONObject jobjp  = (JSONObject) parr.get(y);

                              String item_name = jobjp.get(DatabaseManager.product_name).toString();
                              String item_serial = jobjp.get(DatabaseManager.serial_no).toString();
                              float item_price = Float.parseFloat(String.valueOf(jobjp.get(DatabaseManager.price)));
                              int item_id = Integer.parseInt(String.valueOf(jobjp.get(DatabaseManager._ID)));

                              product = new Product(item_name, null, item_price, null, item_serial, item_id);

                              y = y + 1;
                          }
                          cartProducts.add(product);

                          x++;
                      }
                  }
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }

              return cartProducts;

        }

        @Override
        protected void onPostExecute(List<Product> products) {
            super.onPostExecute(products);

            pbbarCart.setVisibility(View.INVISIBLE);
            if(!products.isEmpty()){
                int cart_product_position = 0;
                for (cart_product_position = 0; cart_product_position < products.size(); cart_product_position++){



                    Product cart_product = products.get(cart_product_position);
                    newRow = new TableRow(getContext() );
                    p_name = new TextView(getContext());
                    s_no = new TextView(getContext());
                    price_view = new TextView(getContext());
                    q_view = new TextView(getContext());
                    total_view = new TextView(getContext());
                    d_view = new ImageButton(getContext());
                    add_quantity = new ImageButton(getContext());
                    reduce_quantity = new ImageButton(getContext());

                    product_id_view = new TextView(getContext());


                    d_view.setImageResource(android.R.drawable.ic_menu_delete);
                    add_quantity.setImageResource(android.R.drawable.arrow_up_float);
                    reduce_quantity.setImageResource(android.R.drawable.arrow_down_float);
                    p_name.setText(cart_product.productName);
                    s_no.setText(cart_product.productSerial);
                    price_view.setText(String.valueOf(cart_product.productPrice));
                    price_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    q_view.setText("1");
                    q_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    product_id_view.setText(String.valueOf(product.product_id));
                    product_id_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    total_view.setText(String.valueOf(cart_product.productPrice));

                    newRow.addView(product_id_view);
                    newRow.addView(p_name);
                    newRow.addView(s_no);
                    newRow.addView(price_view);
                    newRow.addView(q_view);
                    newRow.addView(total_view);
                    newRow.addView(add_quantity);
                    newRow.addView(reduce_quantity);
                    newRow.addView(d_view);

                  if((cart_product_position%2) == 0 ) {

                      newRow.setBackgroundColor(getResources().getColor(R.color.light_gray));

                  }
                    cart_table.addView(newRow, cart_table.getChildCount()-1);



                }
                Log.i("oncountrows", "onCreateView: tablerows counts :  "+ cart_table.getChildCount());

                CartListAdapter cartListAdapter = new CartListAdapter(cart_table, getContext());

                cartListAdapter.setOnClickListener();


            }
        }
    }

}
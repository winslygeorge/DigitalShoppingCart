package com.deepdiver.digitalshoppingcart.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.deepdiver.digitalshoppingcart.LoadImageFromFireStore;
import com.deepdiver.digitalshoppingcart.R;
import com.deepdiver.digitalshoppingcart.data.database.DatabaseManager;
import com.deepdiver.digitalshoppingcart.data.database.OncheckListListener;
import com.deepdiver.digitalshoppingcart.data.database.URLConnector;
import com.deepdiver.digitalshoppingcart.data.model.SessionManager;
import com.deepdiver.digitalshoppingcart.ui.DisplayDetailProduct;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder> implements Filterable {

    public OncheckListListener oncheckList;

    public Context context;
    public View mainview;
    private CardView cardView;

    public ProductListAdapter(List<Product> productList, Context context, View mainView, OncheckListListener oncheckList) {
        this.productList = productList;
        this.context = context;
        this.mainview = mainView;
        this.oncheckList = oncheckList;
        this.modifiedProductList = new ArrayList<>(productList);
    }

    List<Product> productList;

    List<Product> modifiedProductList;




    @NonNull
    @Override
    public ProductListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view  = layoutInflater.inflate(R.layout.list_product_layout, parent, false);

        return new ProductListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListViewHolder holder, int position) {

        if(!productList.isEmpty()){


            holder.currentPosition = position;
            Product product = productList.get(position);
            holder.product_name.setText(product.productName);
            String priceString = new StringBuilder(String.valueOf(product.productPrice)).toString();
            String strfinal = "Ksh. "+priceString;
            holder.product_price.setText(strfinal);
            holder.product_serial.setText(product.productSerial);
            holder.product_id = product.product_id;
            String file = product.productImageUrl;

            if( file != null){
              new LoadImageFromFireStore(context, null, holder.product_image).execute(file);
            }else{

                holder.product_image.setImageResource(R.drawable.ic_menu_camera);
            }
        }

    }

    @Override
    public int getItemCount() {
        return productList.size();

    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredList = new ArrayList<>();
            if(constraint.length() == 0 || constraint == null){

                filteredList.addAll(modifiedProductList);

            }else{

                String stringConst = constraint.toString().toLowerCase().trim();

                for (Product product: modifiedProductList
                     ) {

                    if(product.productName.toLowerCase().trim().contains(stringConst)){

                        filteredList.add(product);
                    }
                }

            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            productList.clear();
            productList.addAll((List<Product>) results.values);
            notifyDataSetChanged();
        }
    };

    public class ProductListViewHolder extends RecyclerView.ViewHolder{

        private final ImageView product_image;
        private final TextView product_name;
        private final TextView product_serial;
        private final TextView product_price;
    //   private final ImageView enlarged_image;
        public int currentPosition ;
        public Bitmap imgFile;
        public final ImageButton addToCartBtn;
        public int product_id;
        public ProductListViewHolder(@NonNull View itemView) {
            super(itemView);

            product_image = itemView.findViewById(R.id.pro_image_list_view);
            product_name = itemView.findViewById(R.id.pro_name_list_view);
            product_serial = itemView.findViewById(R.id.pro_color_list_view);
            product_price = itemView.findViewById(R.id.pro_origin_list_view);
          //  enlarged_image = mainview.findViewById(R.id.enlarged_image_view);
            addToCartBtn = itemView.findViewById(R.id.addToCart_btn);

            product_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context.getApplicationContext(), DisplayDetailProduct.class);
                    intent.putExtra("product_id", product_id);
                    context.startActivity(intent);
                }
            });

            addToCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    cardView = mainview.findViewById(R.id.add_card_card);
                    cardView.setVisibility(View.VISIBLE);
                   RadioGroup group =  mainview.findViewById(R.id.add_group);

                   group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                       @Override
                       public void onCheckedChanged(RadioGroup group, int checkedId) {

                           if(group.getCheckedRadioButtonId() == R.id.add_to_crt){

                               new AddProductToCartAsyc().execute(product_id, currentPosition);
                           }else if(group.getCheckedRadioButtonId() == R.id.add_to_shp_list){

                               new AddProductToShopListAsyc().execute(product_id, currentPosition);
                           }
                       }
                   });


                }
            });
        }
    }
    public class AddProductToCartAsyc extends AsyncTask<Integer, ProgressBar, Boolean>{

        private URLConnector db =  new URLConnector("https://georgosdigital.azurewebsites.net/digitalurlget");
        private int p_id;

        private int currentp;

        @Override
        protected Boolean doInBackground(Integer... strings) {
            p_id = strings[0];
            currentp = strings[1];
            int c_id = new SessionManager(context).sessionPref.getInt("customer_id", -1);
            try {

                if (c_id != -1) {
                    String [] cols = { DatabaseManager.product_id_foreign};
                    String[] s_tring = {DatabaseManager.product_id_foreign, DatabaseManager.customer_id_foreign};
                    String [] s_args = {String.valueOf(p_id), String.valueOf(c_id)};
                    JSONObject jobj  = db.get(DatabaseManager.CART_TABLE, cols, s_tring, s_args);
                    JSONArray arr  = (JSONArray)jobj.get("results");
                    if( arr.size() != 0){

                        return false;
                    }else {


                        ContentValues cv = new ContentValues();

                        cv.put(DatabaseManager.product_id_foreign, p_id);
                        cv.put(DatabaseManager.customer_id_foreign, c_id);
                      JSONObject iobj =  db.handlePostRequest("https://georgosdigital.azurewebsites.net/digitalurlpost", new String[]{DatabaseManager.product_id_foreign, DatabaseManager.customer_id_foreign}, new String[]{String.valueOf(p_id), String.valueOf(c_id)}, "insert", DatabaseManager.CART_TABLE, null, null);


                        if(Integer.parseInt(String.valueOf(iobj.get("resultCode"))) == 200) {
                          return true;
                      }else {
                          return false;
                      }
                    }
                }
            } catch (Exception e) {
                Log.i("errorCart", "doInBackground: cart loading error " + e);

                return false;
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean == true){

                Toast.makeText(context, "Item added cart", Toast.LENGTH_SHORT).show();

                oncheckList.pro_name_checked(p_id);
            }else {

                Toast.makeText(context, "Error adding || Product already exits  in  cart", Toast.LENGTH_SHORT).show();

            }

            if(cardView != null){

                cardView.setVisibility(View.INVISIBLE);
            }
        }
    }


    public class AddProductToShopListAsyc extends AsyncTask<Integer, ProgressBar, Boolean>{

        private URLConnector db = new URLConnector("https://georgosdigital.azurewebsites.net/digitalurlget");

        int product_pos = 0;
        @Override
        protected Boolean doInBackground(Integer... strings) {
            int p_id = strings[0];
            product_pos = strings[1];
            int c_id = new SessionManager(context).sessionPref.getInt("customer_id", -1);
            try {

                if (c_id != -1) {

                    String [] cols = { DatabaseManager.foreign_product_id};
                    String [] s_tring = {DatabaseManager.foreign_product_id , DatabaseManager.foreign_customer_id };
                    String [] s_args = {String.valueOf(p_id), String.valueOf(c_id)};

                    JSONObject iobj = db.get(DatabaseManager.SHOPPING_LIST_TABLE, cols, s_tring, s_args);
                    JSONArray arr = (JSONArray)iobj.get("results");
                    if(arr.size() != 0){
                        return  false;
                    }else {



                        ContentValues cv = new ContentValues();

                        cv.put(DatabaseManager.foreign_product_id, p_id);
                          cv.put(DatabaseManager.foreign_customer_id, c_id);
                          cv.put(DatabaseManager.isChecked, 0);
                       JSONObject oobj =  db.handlePostRequest("https://georgosdigital.azurewebsites.net/digitalurlpost", new String[]{DatabaseManager.foreign_product_id, DatabaseManager.foreign_customer_id, DatabaseManager.isChecked}, new String[]{String.valueOf(p_id), String.valueOf(c_id), String.valueOf(0)},"insert", DatabaseManager.SHOPPING_LIST_TABLE, null, null);


                        if(Integer.parseInt(String.valueOf(oobj.get("resultCode"))) == 200) {
                           return true;
                       }else{
                           return false;
                       }
                    }
                }else{

                    return false;
                }
            } catch (Exception e) {
                Log.i("errorCart", "doInBackground: SHOPPING LIST loading error " + e);

                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean == true){

                Toast.makeText(context, "Item added SHOPPING LIST", Toast.LENGTH_SHORT).show();
            }else{

                Toast.makeText(context, "Error adding Shopping list || Product already exits in  SHOPPING LIST", Toast.LENGTH_SHORT).show();

            }

            if(cardView != null){

                cardView.setVisibility(View.INVISIBLE);
            }
        }
    }

}

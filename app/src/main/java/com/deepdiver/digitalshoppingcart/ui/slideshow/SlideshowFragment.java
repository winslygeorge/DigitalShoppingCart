package com.deepdiver.digitalshoppingcart.ui.slideshow;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deepdiver.digitalshoppingcart.R;
import com.deepdiver.digitalshoppingcart.data.Product;
import com.deepdiver.digitalshoppingcart.data.database.DatabaseManager;
import com.deepdiver.digitalshoppingcart.data.database.URLConnector;
import com.deepdiver.digitalshoppingcart.data.model.SessionManager;
import com.deepdiver.digitalshoppingcart.data.model.ShoppingListAdapter;
import com.deepdiver.digitalshoppingcart.ui.home.HomeFragment;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SlideshowFragment extends Fragment {

    private Fragment ProductFragment = new HomeFragment();
    public List<Integer> fromMain = new ArrayList<>();
    private SlideshowViewModel slideshowViewModel;
    private RecyclerView recyclerView;
    private Product product;
    public List<Product> shoppingListProducts = new ArrayList<>();
    private SQLiteDatabase db;
    protected  ProgressBar pbbarShooping;
    ViewGroup v_group ;
    public  static ShoppingListAdapter shoppingListAdapter;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        v_group = container;

        slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        pbbarShooping  = root.findViewById(R.id.pbbarshopping);
                recyclerView = root.findViewById(R.id.shopping_list_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(linearLayoutManager);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            List<Product> list_p =   new UdateShoppingListAsyc().execute().get();


            if (!list_p.isEmpty()) {


            shoppingListAdapter = new ShoppingListAdapter(list_p, this.getActivity().getApplicationContext());

                recyclerView.setAdapter(shoppingListAdapter);



            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public void setSearchListener(SearchView searchView, Context contx){

        if(searchView != null){

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Toast.makeText(contx, "search query initiated", Toast.LENGTH_LONG).show();

                    SlideshowFragment.this.shoppingListAdapter.getFilter().filter(newText);
                    return false;
                }
            });
        }else {

            Toast.makeText(getContext(), "sv is null", Toast.LENGTH_SHORT).show();
        }
    }
    public class UdateShoppingListAsyc extends AsyncTask<String, ProgressBar, List<Product>>{

        URLConnector db  = new URLConnector("https://georgosdigital.azurewebsites.net/digitalurlget");
        @Override
        protected List<Product> doInBackground(String... strings) {

pbbarShooping.setVisibility(View.VISIBLE);

            String [] selCols = {DatabaseManager.foreign_product_id, DatabaseManager.foreign_customer_id, DatabaseManager.isChecked};

            try{

             JSONObject jsonObject =  db.get(DatabaseManager.SHOPPING_LIST_TABLE, selCols,null, null);

             if(Integer.parseInt(String.valueOf(jsonObject.get("resultCode")))  == 404 ){

                 return null;
             }else {

                 JSONArray jsonArray  = (JSONArray) jsonObject.get("results");
                 int x  = 0;
                 while (x <jsonArray.size()) {

                     JSONObject cartobj = (JSONObject) jsonArray.get(x);

                     int product_id = Integer.parseInt(String.valueOf(cartobj.get(DatabaseManager.foreign_product_id)));

                     int customet_id = Integer.parseInt(String.valueOf(cartobj.get(DatabaseManager.foreign_customer_id)));

                     int is_checked = Integer.parseInt(String.valueOf(cartobj.get(DatabaseManager.isChecked)));

                     if (customet_id == new SessionManager(getContext()).sessionPref.getInt("customer_id", -1)) {
                         String[] selectionString = {DatabaseManager._ID};

                         String[] selCol = {DatabaseManager.product_name, DatabaseManager.product_image, DatabaseManager._ID};

                         String[] selArgs = {String.valueOf(product_id)};

                         JSONObject jobj = db.get(DatabaseManager.PRODUCT_TABLE, selCol, selectionString, selArgs);

                         if (Integer.parseInt(String.valueOf(jobj.get("resultCode"))) == 404) {

                             return null;

                         } else {

                             JSONArray dbCursor = (JSONArray) jobj.get("results");
                             int y = 0;
                             while (y < dbCursor.size()) {

                                 JSONObject pobj = (JSONObject) dbCursor.get(y);
                                 String product_name = (String) pobj.get(DatabaseManager.product_name);
                                 String product_image = (String) pobj.get(DatabaseManager.product_image);
                                 int product_postion = Integer.parseInt(String.valueOf(pobj.get(DatabaseManager._ID)));

                                 String file = product_image;

                                 if (!file.isEmpty()) {
                                     product = new Product(product_name, file, -1, null, null, product_postion, is_checked);
                                 } else {

                                     product = new Product(product_name, null, -1, null, null, product_postion, is_checked);

                                 }

                                 shoppingListProducts.add(product);
                                 y++;
                             }
                         }
                     }
                     x++;
                 }
                     return shoppingListProducts;

             }
            }catch(Exception e){
                Log.e("shoppinglisterr", "doInBackground: onloadshoppinglist", e );
                return shoppingListProducts;
            }

        }


        @Override
        protected void onPostExecute(List<Product> products) {
            super.onPostExecute(products);

            pbbarShooping.setVisibility(View.INVISIBLE);
        }
    }

public void checkItemAddedToCart(int   i, Context context){

             fromMain.add(i);
             new CheckShpListAsync(context).execute(i);
      }

public class CheckShpListAsync extends  AsyncTask<Integer , ProgressBar, Integer>{

    private URLConnector db;
    public Context context;

    public CheckShpListAsync(Context context){
        this.context = context;

    };

    @Override
    protected Integer doInBackground(Integer... integers) {

        int product_id = integers[0];

        db = new URLConnector("https://georgosdigital.azurewebsites.net/digitalurlget");
        String [] cols = {DatabaseManager._ID,DatabaseManager.foreign_product_id};
        String [] s_t = {DatabaseManager.foreign_product_id};
        String [ ] s_a = { String.valueOf(product_id)};
       JSONObject cursor = db.get(DatabaseManager.SHOPPING_LIST_TABLE, cols,  s_t, s_a);

        try {
            if (Integer.parseInt(String.valueOf(cursor.get("resultCode")))!= 404 ){

                ContentValues cv = new ContentValues();
                cv.put(DatabaseManager.isChecked, 1);

                String cl = DatabaseManager.foreign_product_id + " = ?";
                String []ags ={String.valueOf(product_id)};
               JSONObject uobj  =  db.handlePostRequest("https://georgosdigital.azurewebsites.net/digitalurlpost", new String [] {DatabaseManager.isChecked}, new String[]{"1"}, "update", DatabaseManager.SHOPPING_LIST_TABLE, DatabaseManager.foreign_product_id, String.valueOf(product_id));

                if(Integer.parseInt(String.valueOf(uobj.get("resultCode"))) !=  404){
                    return 1;
                }else{

                    return -1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        if(integer != -1){

            Toast.makeText(context, "Product checked in Shopping list", Toast.LENGTH_LONG).show();
        }
    }
}

}
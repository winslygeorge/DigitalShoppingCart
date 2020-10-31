package com.deepdiver.digitalshoppingcart.ui.home;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deepdiver.digitalshoppingcart.R;
import com.deepdiver.digitalshoppingcart.data.Product;
import com.deepdiver.digitalshoppingcart.data.ProductListAdapter;
import com.deepdiver.digitalshoppingcart.data.database.OncheckListListener;
import com.deepdiver.digitalshoppingcart.data.database.URLConnector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.deepdiver.digitalshoppingcart.data.database.DatabaseManager._ID;
import static com.deepdiver.digitalshoppingcart.data.database.DatabaseManager.desc;
import static com.deepdiver.digitalshoppingcart.data.database.DatabaseManager.price;
import static com.deepdiver.digitalshoppingcart.data.database.DatabaseManager.product_image;
import static com.deepdiver.digitalshoppingcart.data.database.DatabaseManager.product_name;
import static com.deepdiver.digitalshoppingcart.data.database.DatabaseManager.serial_no;

public class HomeFragment extends Fragment{

    private OncheckListListener oncheckList;

    private HomeViewModel homeViewModel;

    public List<Product> productList = new ArrayList<>();
    private Product product;
    private SQLiteDatabase db;
    private RecyclerView recyclerView;
    protected URLConnector urlConnector;
    public static ProductListAdapter adapter;
    protected ProgressBar pbbarproduct;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        pbbarproduct  = root.findViewById(R.id.pbarproduct);

                recyclerView = root.findViewById(R.id.product_recycler_view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        recyclerView.setLayoutManager(gridLayoutManager);




        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        new LoadProductsAync().execute();

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

                    HomeFragment.this.adapter.getFilter().filter(newText);
                    return false;
                }
            });
        }else {

            Toast.makeText(getContext(), "sv is null", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof OncheckListListener){

            oncheckList = (OncheckListListener) context;
        }else{

            throw new RuntimeException(context.toString() + " must implement OncheckListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        oncheckList = null;
    }



    public class  LoadProductsAync extends AsyncTask<String, ProgressBar, JSONObject>{

        private URLConnector urlConnector ;
        @Override
        protected JSONObject doInBackground(String... strings) {

            pbbarproduct.setVisibility(View.VISIBLE);

            String[] selectionsColumns = {_ID, product_name, serial_no, desc, price, product_image};

            urlConnector  = new URLConnector("https://georgosdigital.azurewebsites.net/digitalurlget");

            JSONObject productobj  = urlConnector.get("product_table", selectionsColumns, null, null);

            return productobj;
        }
        
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            pbbarproduct.setVisibility(View.INVISIBLE);
            if (jsonObject != null) {


                try {
                    JSONArray data  = (JSONArray) jsonObject.get("results");

                    int x = 0;
                    while (x < data.size()) {

                        JSONObject productat  = (JSONObject)data.get(x);

                        String name = (String)productat.get(product_name);
                        String desc = (String)productat.get("product_desc");
                        float price = Float.parseFloat(String.valueOf(productat.get("price")));
                        String seial = (String) productat.get(serial_no);
                        String image_name = productat.get(product_image).toString();
                        int product_id = Integer.parseInt(String.valueOf(productat.get(_ID)));


                        if (image_name != null) {
                            product = new Product(name, image_name, price, desc, seial, product_id);
                            Log.i("onloadproduct", "onLoadFinished: image exits");
                        } else {

                            product = new Product(name, null, price, desc, seial, product_id);

                            Log.i("onloadProduct", "onLoadFinished: image doesnt exist");
                        }

                        productList.add(product);
                        x++;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                adapter = new ProductListAdapter(productList, getContext(), getView(), oncheckList);

                recyclerView.setAdapter(adapter);
            }else{

                Toast.makeText(getContext(), "The Store is Empty", Toast.LENGTH_LONG).show();
            }

        }
    }
}
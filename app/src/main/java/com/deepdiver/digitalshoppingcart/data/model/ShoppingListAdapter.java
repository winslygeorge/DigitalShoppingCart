package com.deepdiver.digitalshoppingcart.data.model;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deepdiver.digitalshoppingcart.LoadImageFromFireStore;
import com.deepdiver.digitalshoppingcart.R;
import com.deepdiver.digitalshoppingcart.data.Product;
import com.deepdiver.digitalshoppingcart.data.database.DatabaseManager;
import com.deepdiver.digitalshoppingcart.data.database.URLConnector;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder> implements Filterable {
    public ShoppingListAdapter(List<Product> shopping_list, Context context) {
        this.shopping_list = shopping_list;
        this.shopping_listAll = new ArrayList<>(shopping_list);
        this.context = context;
    }

    public List<Product> shopping_list;
    public List<Product> shopping_listAll;
    public Context context;

    @NonNull
    @Override
    public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.shopping_list_layout, parent, false);
        return new ShoppingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int position) {
        if(!shopping_list.isEmpty()){

            Product product = shopping_list.get(position);
            holder.list_pos = position;
            holder.name_view.setText(product.productName);
            if(product.isChecked == 1){

                holder.isObtained.setChecked(true);
            }else{
                holder.isObtained.setChecked(false);
            }



            if(product.productImageUrl != null){

               new LoadImageFromFireStore(context,null,holder.imageView).execute(product.productImageUrl);
            }

            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                  new DeleteFromShoppingCart().execute(String.valueOf(product.product_id));
                }
            });

        }


    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        if(!payloads.isEmpty()){
            Boolean b = (Boolean) payloads.get(0);

            holder.isObtained.setChecked(true);
        }else{

            onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return shopping_list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredList = new ArrayList<>();
            if(constraint.length() == 0|| constraint.length() == 0){

                filteredList.addAll(shopping_listAll);
            }else{
                for (Product item: shopping_listAll
                     ) {

                    if(item.productName.toLowerCase().contains(constraint.toString().toLowerCase().trim())){
                        filteredList.add(item);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            shopping_list.clear();
            shopping_list.addAll((List) results.values );
            notifyDataSetChanged();
        }
    };

    public static class ShoppingListViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        private final TextView name_view;
        private final CheckBox isObtained;
        private final ImageButton deleteBtn;

        public int list_pos;

        public ShoppingListViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.shopping_list_image_view);
            name_view = itemView.findViewById(R.id.shopping_list_product_name);
            isObtained = itemView.findViewById(R.id.is_product_obtained);
            deleteBtn  = itemView.findViewById(R.id.delete_from_slist);

        }
    }

    public class DeleteFromShoppingCart extends AsyncTask<String, ProgressBar, Boolean>{

        @Override
        protected Boolean doInBackground(String... integers) {

            URLConnector urlConnector  = new URLConnector("https://georgosdigital.azurewebsites.net/digitalurlget");


            if(Integer.parseInt(String.valueOf(  urlConnector.delete(DatabaseManager.SHOPPING_LIST_TABLE, new String[]{DatabaseManager.foreign_product_id}, new String[]{integers[0]}).get("resultCode"))) == 200){

                return true;
            }

return false;

        }
    }
}

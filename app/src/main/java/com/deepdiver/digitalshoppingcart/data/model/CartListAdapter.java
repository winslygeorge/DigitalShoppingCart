package com.deepdiver.digitalshoppingcart.data.model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.deepdiver.digitalshoppingcart.data.database.DatabaseManager;
import com.deepdiver.digitalshoppingcart.data.database.URLConnector;

import java.util.ArrayList;
import java.util.List;
public class CartListAdapter{
    
    TableLayout cart_tableLayout;
    public Context context;
    private int rowCursorAtPosition;
    private TextView total;

    List<Float> totalist = new ArrayList<>();
    private TableRow totalRow;
    private TextView t_view;

    public CartListAdapter(TableLayout cart_tableLayout, Context context) {
        this.cart_tableLayout = cart_tableLayout;
        this.context = context;

        totalRow = (TableRow) cart_tableLayout.getChildAt(cart_tableLayout.getChildCount()-1);
        t_view = (TextView) totalRow.getChildAt(1);

    }

    public int getTableRowCount(){
        return cart_tableLayout.getChildCount();
    }

    public void setOnClickListener(){
        int rowPosition = 0;

        for (rowPosition = 0; rowPosition < getTableRowCount(); rowPosition++){


            if(rowPosition < 2 || rowPosition >= cart_tableLayout.getChildCount()-1){
                continue;
            }

            rowCursorAtPosition = rowPosition;
            TableRow tableRow = (TableRow) cart_tableLayout.getChildAt(rowPosition);

            total = (TextView) tableRow.getChildAt(5);

            float td = Float.parseFloat(total.getText().toString());
            int columnCount = tableRow.getChildCount();

            totalist.add(td);

            int columnPosition = 0;
            for( columnPosition = 0; columnPosition < columnCount; columnPosition++){




                if(columnPosition == 6){

                    ImageButton addQ = (ImageButton) tableRow.getChildAt(columnPosition);

                    addQ.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("addQ", "onClick: add quantity button clicked");

                            TextView q_text = (TextView) tableRow.getChildAt(4);
                            TextView one_item_pice = (TextView) tableRow.getChildAt(3);
                            TextView total = (TextView) tableRow.getChildAt(5);

                            float previous_total = Float.parseFloat(t_view.getText().toString());

                            float previous_specific_item_total = Float.parseFloat(total.getText().toString());


                            int q = Integer.parseInt(q_text.getText().toString())+1;
                            float t = Float.parseFloat(one_item_pice.getText().toString()) * q;
                            q_text.setText(String.valueOf(q));
                            total.setText(String.valueOf(t));

                            float current_total = (previous_total - previous_specific_item_total) + t;

                            t_view.setText(String.valueOf(current_total));
                        }
                    });
                }else if(columnPosition == 7){

                    ImageButton redQ = (ImageButton) tableRow.getChildAt(columnPosition);

                    redQ.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            TextView q_text = (TextView) tableRow.getChildAt(4);
                            TextView total = (TextView) tableRow.getChildAt(5);
                            TextView one_item_pice = (TextView) tableRow.getChildAt(3);

                            float previous_total = Float.parseFloat(t_view.getText().toString());


                            float previous_specific_item_total = Float.parseFloat(total.getText().toString());

                            if(previous_specific_item_total != 0){

                                int q = Integer.parseInt(q_text.getText().toString())-1;
                                float t = Float.parseFloat(one_item_pice.getText().toString()) * q;
                                q_text.setText(String.valueOf(q));
                                total.setText(String.valueOf(t));

                                float current_total = (previous_total - previous_specific_item_total) + t;

                                t_view.setText(String.valueOf(current_total));
                            }


                        }
                    });
                }else if(columnPosition == 8){

                    ImageButton delProduct = (ImageButton) tableRow.getChildAt(columnPosition);

                    delProduct.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            TextView _id_view = (TextView) tableRow.getChildAt(0);
                            int p_id = Integer.parseInt( _id_view.getText().toString());


                            float previous_total = Float.parseFloat(t_view.getText().toString());

                            float previous_specific_item_total = Float.parseFloat(total.getText().toString());


                            int [] mInt = {p_id, rowCursorAtPosition};

                            new deleteCartAsync().execute(p_id, rowCursorAtPosition);


                            float current_total = (previous_total - previous_specific_item_total);

                            t_view.setText(String.valueOf(current_total));
                        }
                    });
                }
            }
        }

        int k = totalist.size();
        float doubl_t = 0;
        while(k > 0){
            doubl_t +=totalist.get(k-1);
            k--;
        }
        t_view.setText(String.valueOf(doubl_t));
    }

    public class deleteCartAsync extends AsyncTask<Integer, ProgressBar, Integer>{

        private URLConnector db;

        @Override
        protected Integer doInBackground(Integer... strings) {

            int product_id = strings[0];
            db = new URLConnector("https://georgosdigital.azurewebsites.net/digitalurlget");
            String [] deleteClause = {String.valueOf(DatabaseManager.product_id_foreign)} ;
            String [] strings1 = {String.valueOf(product_id)};
            try {
               if(Integer.parseInt(String.valueOf( db.delete(DatabaseManager.CART_TABLE, deleteClause, strings1).get("resultCode"))) == 200) {
                   Log.e("oncartdelete", "doInBackground: update successfull");

                   return strings[1];
               }else{
                   Log.e("oncartdelete", "doInBackground: failed update" );
                   return -1;
               }
            }catch (Exception e){
                Log.e("oncartdelete", "doInBackground: failed update", e );
                return -1;
            }

        }

        @Override
        protected void onPostExecute(Integer aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean != -1){

                Toast.makeText(context, "product successfully deleted", Toast.LENGTH_SHORT).show();
                cart_tableLayout.getChildAt(aBoolean).setVisibility(View.GONE);
                cart_tableLayout.removeViewAt(aBoolean);



            }else{

                Toast.makeText(context, "product unsuccessfully deleted", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void addTotal(){
        int t_pos = totalist.size()-1;
        float TOTAL = 0;
        while(t_pos > 0){

           TOTAL  = TOTAL +  totalist.get(t_pos);
            t_pos = t_pos -1;
        }



        t_view.setText(String.valueOf(TOTAL));
    }
}
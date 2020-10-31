package com.deepdiver.digitalshoppingcart.data;

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

public class ProductUpdator {

        TableLayout cart_tableLayout;
        public Context context;
        private int rowCursorAtPosition;
        private TextView total;

        List<Float> totalist = new ArrayList<>();
        private TableRow totalRow;
        private TextView t_view;

        public ProductUpdator(TableLayout cart_tableLayout, Context context) {
            this.cart_tableLayout = cart_tableLayout;
            this.context = context;

        }

        public int getTableRowCount(){
            return cart_tableLayout.getChildCount();
        }

        public void setOnClickListener(){
            int rowPosition = 0;

            for (rowPosition = 0; rowPosition < getTableRowCount(); rowPosition++){


                if(rowPosition < 2){
                    continue;
                }

                rowCursorAtPosition = rowPosition;
                TableRow tableRow = (TableRow) cart_tableLayout.getChildAt(rowPosition);

                int columnCount = tableRow.getChildCount();


                int columnPosition = 0;
                for( columnPosition = 0; columnPosition < columnCount; columnPosition++){




                    if(columnPosition == 5){

                        ImageButton addQ = (ImageButton) tableRow.getChildAt(columnPosition);

                        addQ.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("addQ", "onClick: add quantity button clicked");

                                TextView q_text = (TextView) tableRow.getChildAt(4);

                                int q = Integer.parseInt(q_text.getText().toString())+1;
                                q_text.setText(String.valueOf(q));
                            }
                        });
                    }else if(columnPosition == 6){

                        ImageButton redQ = (ImageButton) tableRow.getChildAt(columnPosition);

                        redQ.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                TextView q_text = (TextView) tableRow.getChildAt(4);

                                int q = Integer.parseInt(q_text.getText().toString())-1;
                                q_text.setText(String.valueOf(q));


                            }
                        });
                    }else if(columnPosition == 7){

                        ImageButton delProduct = (ImageButton) tableRow.getChildAt(columnPosition);

                        delProduct.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                TextView _id_view = (TextView) tableRow.getChildAt(0);
                                int p_id = Integer.parseInt( _id_view.getText().toString());




                                int [] mInt = {p_id, rowCursorAtPosition};

                                new deleteProductAsync().execute(p_id, rowCursorAtPosition);


                            }
                        });
                    }
                }
            }
        }

        public class deleteProductAsync extends AsyncTask<Integer, ProgressBar, Integer> {

            private URLConnector db;

            @Override
            protected Integer doInBackground(Integer... strings) {

                int product_id = strings[0];
                db = new URLConnector("https://georgosdigital.azurewebsites.net/digitalurlget");
                String[] deleteClause = {DatabaseManager._ID};
                String [] strings1 = {String.valueOf(product_id)};
                try {
                   if( Integer.parseInt(String.valueOf(db.delete(DatabaseManager.PRODUCT_TABLE, deleteClause, strings1).get("resultCode"))) == 200) {
                       Log.e("oncartdelete", "doInBackground: update successfull");

                       return strings[1];
                   }else{
                       Log.e("oncartdelete", "doInBackground: failed update");
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


    }

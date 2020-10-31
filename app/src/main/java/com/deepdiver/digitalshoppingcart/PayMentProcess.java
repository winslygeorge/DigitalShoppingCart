package com.deepdiver.digitalshoppingcart;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.deepdiver.digitalshoppingcart.data.database.DatabaseManager;
import com.deepdiver.digitalshoppingcart.data.database.URLConnector;
import com.google.android.material.snackbar.Snackbar;

import org.json.simple.JSONObject;

public class PayMentProcess extends AppCompatActivity {

    private CardView tans_in_process;
    public ConstraintLayout constraintLayout;

    public Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_ment_process);

        constraintLayout = findViewById(R.id.payment_const_layout);

        Button ok_btn = findViewById(R.id.ok_mpesa_pesa);

        tans_in_process = findViewById(R.id.tans_res_card_view);

        int custId = getIntent().getIntExtra("custId", 0);

                context = getApplicationContext();
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tans_in_process.setVisibility(View.VISIBLE);



                Thread A = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Thread.sleep(3600);

                            tans_in_process.setVisibility(View.INVISIBLE);



                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }finally {

                            tans_in_process.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                Thread trans = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Thread.sleep(3600);

                            tans_in_process.setVisibility(View.INVISIBLE);

                            new PayCartDeleteAsync(custId).execute();

                            Log.i("onTrans", "run: on trans  : transaction was successfull");


                            Snackbar.make(constraintLayout, "Transaction was successfull \n Your receipt was printed for you", Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(getResources().getColor(R.color.progress_background_tint))
                                    .setTextColor(getResources().getColor(R.color.white))
                                    .show();

                            Thread.sleep(1800);

                            Intent intent = new Intent(PayMentProcess.this, Product_List.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(intent);

                            finish();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }finally {

                            tans_in_process.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                trans.start();
            }
        });





    }

    public class PayCartDeleteAsync extends AsyncTask<String, ProgressBar, Boolean>{

        URLConnector db = new URLConnector("https://georgosdigital.azurewebsites.net/digitalurlget");
 protected int custId;
        public PayCartDeleteAsync(int custId) {
            this.custId = custId;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
               JSONObject payres =  db.delete(DatabaseManager.CART_TABLE, new String[]{DatabaseManager.customer_id_foreign}, new String[]{String.valueOf(custId)});
                JSONObject shopres =  db.delete(DatabaseManager.SHOPPING_LIST_TABLE, new String[]{DatabaseManager.foreign_customer_id}, new String[]{String.valueOf(custId)});
              if(Integer.parseInt(String.valueOf(payres.get("resultCode"))) == 200 && Integer.parseInt(String.valueOf(shopres.get("resultCode"))) == 200 ) {


                  return true;
              }else{
                  return false;
              }
            } catch (Exception e) {

                Log.e("ondeletingpaidcart", "doInBackground: error deleting cart", e);

                return false;
            }

        }

    }
}
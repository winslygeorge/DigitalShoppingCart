package com.deepdiver.digitalshoppingcart;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.deepdiver.digitalshoppingcart.data.database.DatabaseManager;
import com.deepdiver.digitalshoppingcart.data.database.OncheckListListener;
import com.deepdiver.digitalshoppingcart.data.database.URLConnector;
import com.deepdiver.digitalshoppingcart.data.model.SessionManager;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.simple.JSONArray;

public class BarScanner extends AppCompatActivity {

    public SurfaceView surfaceView;
    public CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    public TextView textView;
    private CardView promt_cart_card;
    private TextView res_bar;
    public OncheckListListener oncheckListListener;

    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_scanner);

        surfaceView = findViewById(R.id.can_suface);
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(420, 480).build();

        textView = findViewById(R.id.barcode_result_view);

        res_bar = findViewById(R.id.res_scanner);

        context = getApplicationContext();

        promt_cart_card = findViewById(R.id.prompt_scan_to_cart);

        Button accepted = findViewById(R.id.accept_btn_to_cart);
        Button declined = findViewById(R.id.decline_to_cart);

        accepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(BarScanner.this,
                        "product accepted", Toast.LENGTH_SHORT).show();
                new AddProductToCartScannerAsyc().execute(3);
            }
        });

        declined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BarScanner.this,
                        "product declined", Toast.LENGTH_SHORT).show();
                promt_cart_card.setVisibility(View.INVISIBLE);
            }
        });

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                try {

                    cameraSource.start(holder);

                } catch (Exception e) {
                    Log.e("onScanbarcode", "surfaceCreated: ", e);
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> qrCode = detections.getDetectedItems();
                if (qrCode.size() != 0) {

                   BarScanner.this.promt_cart_card.setVisibility(View.VISIBLE);
                    // promt_cart_card.setVisibility(View.VISIBLE);

                    res_bar.setText(qrCode.valueAt(0).displayValue);

                    res_bar.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(3000);

                            textView.setText(qrCode.valueAt(0).displayValue);
                        }
                    });



                }
            }
        });

    }


    public class AddProductToCartScannerAsyc extends AsyncTask<Integer, ProgressBar, Boolean> {

        private URLConnector db = new URLConnector("https://georgosdigital.azurewebsites.net/digitalurlget");
        private int p_id;

        private int currentp;

        @Override
        protected Boolean doInBackground(Integer... strings) {
            p_id = strings[0];
          //  currentp = strings[1];
            int c_id = new SessionManager(context).sessionPref.getInt("customer_id", -1);
            try {

                if (c_id != -1) {
                    String[] cols = {DatabaseManager.product_id_foreign};
                    String[] s_tring = {DatabaseManager.product_id_foreign};
                    String[] s_args = {String.valueOf(p_id)};
                    JSONArray arr = (JSONArray) db.get(DatabaseManager.CART_TABLE, cols, s_tring, s_args).get("results");
                    if (arr.size()!=0 ) {

                        return false;
                    } else {
                       if(Integer.parseInt(String.valueOf( db.handlePostRequest("https://georgosdigital.azurewebsites.net/digitalurlpost", new String[]{DatabaseManager.product_id_foreign,DatabaseManager.customer_id_foreign}, new String[]{String.valueOf(p_id), String.valueOf(c_id)},"insert",DatabaseManager.CART_TABLE, null, null).get("resultCode"))) == 200 ) {

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

                Toast.makeText(getApplicationContext(), "Item added cart", Toast.LENGTH_SHORT).show();

              // oncheckListListener.pro_name_checked(p_id);
            }else {

                Toast.makeText(getApplicationContext(), "Error adding || Product already exits  in  cart", Toast.LENGTH_SHORT).show();

            }

            if(promt_cart_card!= null){

                promt_cart_card.setVisibility(View.INVISIBLE);
            }
        }
    }

}
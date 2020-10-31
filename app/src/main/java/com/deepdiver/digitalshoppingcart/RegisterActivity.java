package com.deepdiver.digitalshoppingcart;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.deepdiver.digitalshoppingcart.data.database.DatabaseManager;
import com.deepdiver.digitalshoppingcart.data.database.URLConnector;
import com.deepdiver.digitalshoppingcart.ui.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {

    protected EditText efname;

    protected EditText esname;
    protected EditText eemail;
    protected EditText eusername;
    protected EditText econemail;
    protected EditText eaddress;
    protected Spinner ecity;
    protected EditText epasswd;
    protected EditText econpasswd;
    protected ProgressBar progressBar;


    private TextView errorTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        efname  = findViewById(R.id.f_name_view);
        esname  = findViewById(R.id.s_name_view);
        eusername = findViewById(R.id.u_name_view);
        eaddress = findViewById(R.id.adress_view);
        eemail = findViewById(R.id.email_view);
        econemail = findViewById(R.id.con_email_view);
        epasswd = findViewById(R.id.passwd_view);
        econpasswd = findViewById(R.id.con_passwd_view);
        ecity = findViewById(R.id.city_spinner);
        errorTxt = findViewById(R.id.error_text);

                Button btn = findViewById(R.id.submit_btn);


                btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFields();
            }
        });

    }




    public void getFields(){

        String firstName = efname.getText().toString();
        String secondName = esname.getText().toString();
        String userName = eusername.getText().toString();
        String email = eemail.getText().toString();
        String password = epasswd.getText().toString();
        String conPassword = econpasswd.getText().toString();
        String address = eaddress.getText().toString();
        String conEmail = econemail.getText().toString();

        String city = ecity.getSelectedItem().toString();

        if(!firstName.isEmpty()&& !secondName.isEmpty()&& !userName.isEmpty()&& !email.isEmpty() && !conEmail.isEmpty() && !password.isEmpty() && !conPassword.isEmpty() && !city.isEmpty() && !address.isEmpty()) {
            if (password.matches(conPassword)) {

                if (email.matches(conEmail)) {

                    if (password.length() > 6) {


                        Log.i( "onPostCreateRegister", userName);

                        new LoadNewUserData(getApplicationContext()).execute(firstName, secondName, userName, email, address, city, password);

                    } else {

                        errorTxt.setText("*The Password Length must be more than 6");
                        errorTxt.setVisibility(View.VISIBLE);
                    }


                } else {

                    errorTxt.setText("*Email do not match");
                    errorTxt.setVisibility(View.VISIBLE);

                }

            } else {

                errorTxt.setText("*Password do not match");
                errorTxt.setVisibility(View.VISIBLE);
            }
        }else {
            errorTxt.setText("*There is an Empty Field");
            errorTxt.setVisibility(View.VISIBLE);

        }
    }

    public class LoadNewUserData extends AsyncTask<String, ProgressBar, Boolean>{

        Context context;
        public LoadNewUserData(Context cont){
            this.context = cont;
        }
        @Override
        protected Boolean doInBackground(String... strings) {

            URLConnector urlConnector = new URLConnector("");

            String[] fields = {DatabaseManager.first_name, DatabaseManager.second_name, DatabaseManager.user_name, DatabaseManager.email, DatabaseManager.address, DatabaseManager.city, DatabaseManager.password};
            String [] values = {strings[0], strings[1], strings[2], strings[3], strings[4], strings[5],strings[6]};

           if(Integer.parseInt(String.valueOf( urlConnector.handlePostRequest("https://georgosdigital.azurewebsites.net/digitalurlpost", fields, values, "insert", DatabaseManager.CUSTOMER_TABLE, null, null)))== 200){
               return true;
           }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean == true){

                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }else{

                Toast.makeText(context, "Error Adding User Please Try Again", Toast.LENGTH_LONG).show();

            }
        }
    }
}
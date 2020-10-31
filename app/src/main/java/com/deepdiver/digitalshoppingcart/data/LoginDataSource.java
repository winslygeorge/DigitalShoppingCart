package com.deepdiver.digitalshoppingcart.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.deepdiver.digitalshoppingcart.data.database.DatabaseManager;
import com.deepdiver.digitalshoppingcart.data.database.URLConnector;
import com.deepdiver.digitalshoppingcart.data.model.LoggedInUser;
import com.deepdiver.digitalshoppingcart.data.model.SessionManager;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {


    public Result<LoggedInUser> login(String username, String password, Context context) {

        String[] selectionColumns = {DatabaseManager.user_name,DatabaseManager.email, DatabaseManager.password , DatabaseManager._ID};
        String[] queryString =  {DatabaseManager.email,  DatabaseManager.password };
        String [] queryArgs = {username, password};
        URLConnector db  = new URLConnector("https://georgosdigital.azurewebsites.net/digitalurlget");
        try {
            // TODO: handle loggedInUser authentication

            JSONObject cursor = new LoadLoginDataAsync(db, selectionColumns, queryString, queryArgs).execute().get();

            Log.i( "loginapi", String.valueOf(cursor.get("resultCode")));

            Toast.makeText(context, String.valueOf(cursor.get("resultCode")), Toast.LENGTH_LONG);

            if(Integer.parseInt(String.valueOf(cursor.get("resultCode")))  == 200) {
               // Log.i( "loginapiconfirm", String.valueOf((int)cursor.get("resultCode")));
                JSONArray jsonArray = (JSONArray) cursor.get("results");

                Log.i("loginArray", "loginin");

                LoggedInUser customer_user = null;
                if (jsonArray.size() != 0) {

                    JSONObject uobj  = (JSONObject) jsonArray.get(0);
                    int u_id = Integer.parseInt(String.valueOf(uobj.get((DatabaseManager._ID))));
                    String user_name = (String)uobj.get((DatabaseManager.user_name));
                    String email = uobj.get((DatabaseManager.email)).toString();

                    if (new SessionManager(context).saveUserSession(email, password, user_name, u_id)) {

                        customer_user = new LoggedInUser("1234", username);
                    } else {

                        return new Result.Error(new IOException("Error logging in", new Exception("in correct credidentials")));
                    }

                } else {
                    return new Result.Error(new IOException("Error logging in", new Exception("in correct credidentials")));

                }

                return new Result.Success<>(customer_user);
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
        return new Result.Error(new IOException("Error logging in", new Exception()));
    }

    public void logout() {
        // TODO: revoke authentication
    }

    public class LoadLoginDataAsync extends AsyncTask<String, ProgressBar, JSONObject>{

        URLConnector db;

        String[] selectionColumns;
        String[] queryString;
        String[] queryArgs;

        public LoadLoginDataAsync(URLConnector db, String[] selectionColumns, String[] queryString, String[] queryArgs){
            this.db = db;
            this.queryArgs = queryArgs;
            this.selectionColumns = selectionColumns;
            this.queryString = queryString;
        }
        @Override
        protected JSONObject doInBackground(String... strings) {

            return db.get(DatabaseManager.CUSTOMER_TABLE, selectionColumns,queryString , queryArgs);

        }
    }
}
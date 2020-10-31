package com.deepdiver.digitalshoppingcart.data.model;

import android.content.Context;
import android.content.SharedPreferences;


public class SessionManager {

    public SharedPreferences sessionPref;

    public  SharedPreferences.Editor editor;

    public SessionManager(Context context){

      sessionPref =   context.getSharedPreferences("login_session", Context.MODE_PRIVATE);

      editor = sessionPref.edit();
      editor.apply();
    }

    public boolean saveUserSession(String email, String passwd, String username,  int u_id){

        if(!email.isEmpty() && !passwd.isEmpty() && !username.isEmpty()){

            editor.putString("username", username);
            editor.putString("email", email);
            String islog = email+":"+passwd;
            editor.putString("user_loggedin", islog);
            editor.putInt("customer_id", u_id);
            editor.commit();

            return true;
        }else {

            return false;
        }


    }

    public boolean retriveSession(){

        String islogged = sessionPref.getString("user_loggedin", null);

        if(islogged != null){

            return true;
        }else{

            return false;
        }
    }

    public void logoutSession(){
        editor.remove("user_loggedin");
        editor.commit();
    }
}

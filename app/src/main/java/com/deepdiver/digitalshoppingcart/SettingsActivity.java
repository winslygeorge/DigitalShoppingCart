package com.deepdiver.digitalshoppingcart;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.deepdiver.digitalshoppingcart.data.database.DatabaseManager;
import com.deepdiver.digitalshoppingcart.data.database.URLConnector;
import com.deepdiver.digitalshoppingcart.ui.login.LoginActivity;

import org.json.simple.JSONObject;

import java.util.concurrent.ExecutionException;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.images__18_));
        }


    }

    public  static class SettingsFragment extends PreferenceFragmentCompat {


        private EditTextPreference user_pref;
        private EditTextPreference email_pref;
        private SwitchPreference login_switch;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            user_pref = getPreferenceScreen().findPreference("usernameprefkey");
            email_pref = getPreferenceScreen().findPreference("email_pref_key");

            login_switch = getPreferenceScreen().findPreference("logoutpref");

            user_pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    try {
                        String pref = new UpdateUserAccountAsync(getActivity().getApplicationContext()).execute((String)newValue).get();

                        preference.setSummary(pref);
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("login_session", MODE_PRIVATE).edit();
                        editor.putString("username", pref);
                        editor.apply();

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });

            email_pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    try {
                        String pref = new UpdateUserAccountAsync(getActivity().getApplicationContext()).execute((String)newValue).get();

                        preference.setSummary(pref);

                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("login_session", MODE_PRIVATE).edit();
                        editor.putString("email", pref);
                        editor.apply();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });

            login_switch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    SwitchPreference switchPreference = (SwitchPreference)preference;

                    getActivity().getSharedPreferences("login_session", MODE_PRIVATE).getAll().clear();
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("login_session", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                    return true;
                }
            });
        }

        @Override
        public void onResume() {
            super.onResume();

             user_pref.setSummary((CharSequence) getActivity().getSharedPreferences("login_session", MODE_PRIVATE).getString("username", "username"));
             email_pref.setSummary((String) getActivity().getSharedPreferences("login_session", MODE_PRIVATE).getString("email", ""));
             login_switch.setSwitchTextOn("logged in");
        }
    }

    public static class UpdateUserAccountAsync extends AsyncTask<String, ProgressBar, String>{
        private  Context contx;
        private final URLConnector db;

        public UpdateUserAccountAsync(Context contex){

            this.contx = contex;
            db = new URLConnector("");

        }

        @Override
        protected String doInBackground(String... strings) {
            String prefs = strings[0];

            if(prefs.contains("@")){

                String selS = DatabaseManager.email + " = ? ";
                String prevEmail = this.contx.getSharedPreferences("login_session", MODE_PRIVATE).getString("email", "");
                if(!prevEmail.isEmpty() || prevEmail != null){

                    String [] selA = {prevEmail};
                    ContentValues cv = new ContentValues();
                    cv.put(DatabaseManager.email, prefs);
               JSONObject jsonObject = db.handlePostRequest("https://georgosdigital.azurewebsites.net/digitalurlpost", new String[]{DatabaseManager.email}, new String[]{prefs},"update", DatabaseManager.CUSTOMER_TABLE, DatabaseManager.email, prevEmail);
                    try {
                        if(Integer.parseInt(String.valueOf(jsonObject.get("resultCode")))== 200) {
                            return prefs;
                        }else{

                            return  prevEmail;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return prefs;

            }else{

            String prevUser = this.contx.getSharedPreferences("login_session", MODE_PRIVATE).getString("username", "");
            String selS = DatabaseManager.user_name + " = ? ";
            if(!prevUser.isEmpty()){

                String [] selA = {prevUser};
                ContentValues cv = new ContentValues();
                cv.put(DatabaseManager.user_name, prefs);
                try {
                    if(Integer.parseInt(String.valueOf(db.handlePostRequest("https://georgosdigital.azurewebsites.net/digitalurlpost", new String[]{DatabaseManager.user_name}, new String[]{prefs},"update", DatabaseManager.CUSTOMER_TABLE, DatabaseManager.user_name, prevUser).get("resultCode"))) == 200){
                        return prefs;
                    }else{

                        return prevUser;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }


}
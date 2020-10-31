package com.deepdiver.digitalshoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.deepdiver.digitalshoppingcart.ui.login.LoginActivity;

public class SplashScreenLauncher extends AppCompatActivity {
    LogoLauncher logoLauncher = new LogoLauncher();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen_launcher);
        getSupportActionBar().hide();
        logoLauncher.start();

    }

    private class LogoLauncher extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(SplashScreenLauncher.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            SplashScreenLauncher.this.finish();
        }
    }
}
package app.taxipizza.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;


import app.taxipizza.R;
import app.taxipizza.Utils.Utils;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Utils.getCurrentUser(getApplicationContext()) != null)
                    startActivity(new Intent(SplashScreenActivity.this, DrawerActivity.class));
                else
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                finish();
            }
        }, 2000);

    }
}

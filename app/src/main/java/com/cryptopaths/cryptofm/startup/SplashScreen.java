package com.cryptopaths.cryptofm.startup;

import android.support.v7.app.AppCompatActivity;
import android.os.*;
import android.content.Intent;
import android.view.*;

import com.cryptopaths.cryptofm.R;

/**
 * Created by patriot on 1/14/17.
 */

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                        // This method will be executed once the timer is over
                        // Start your app main activity
                        Intent i = new Intent(SplashScreen.this, InitActivity.class);
                        startActivity(i);

                        // close this activity
                        finish();


            }
}


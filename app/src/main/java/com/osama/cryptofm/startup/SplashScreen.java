package com.osama.cryptofm.startup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by patriot on 1/14/17.
 * Splash activity which just starts another activity
 */

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs=getSharedPreferences("done",Context.MODE_PRIVATE);
        if(prefs.getBoolean("done",false)){
            Intent intent = new Intent(this,UnlockDbActivity.class);
            intent.putExtra("username",prefs.getString("username","test"));
            //clear the stack
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent,1);
            finish();
            return;
        }

        SharedPreferences preferences=getPreferences(Context.MODE_PRIVATE);
        boolean isFirstRun=preferences.getBoolean("run",true);
        if(isFirstRun){
            SharedPreferences.Editor editor=preferences.edit();
            editor.putBoolean("run",false);
            editor.apply();
            editor.commit();
            Intent i = new Intent(SplashScreen.this, PreStartActivity.class);
            startActivity(i);

        }else{
            Intent i = new Intent(SplashScreen.this, InitActivity.class);
            startActivity(i);
        }
        // close this activity
        finish();


    }
}


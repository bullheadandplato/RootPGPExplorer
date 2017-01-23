package com.cryptopaths.cryptofm.startup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.utils.ActionHandler;

public class OptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.key_options);
    }
    @ActionHandler(layoutResource = R.id.button_generate_key)
    public void chooseNextActivity(View view){
        if(view.getId()==R.id.button_generate_key){
            //start the init activity
            Intent intent=new Intent(this,InitActivity.class);
            startActivity(intent);
        }else{
            Intent intent=new Intent(this,KeySelectActivity.class);
            startActivity(intent);
        }
    }
}

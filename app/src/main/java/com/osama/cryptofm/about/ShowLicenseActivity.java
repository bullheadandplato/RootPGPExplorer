package com.osama.cryptofm.about;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.osama.cryptofm.R;

public class ShowLicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_license);
        //get the licence string and show it
        ((TextView)findViewById(R.id.show_license_textview)).setText(getIntent().getExtras().getString("license"));
    }
}

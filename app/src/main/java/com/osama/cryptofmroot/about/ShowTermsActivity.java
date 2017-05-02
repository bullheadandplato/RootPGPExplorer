package com.osama.cryptofmroot.about;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.osama.cryptofmroot.R;

public class ShowTermsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_terms);
        TextView termsandprivacy=(TextView)findViewById(R.id.terms_and_privacy_view_textview);
        termsandprivacy.setText(getString(R.string.terms_and_condition));
    }
}

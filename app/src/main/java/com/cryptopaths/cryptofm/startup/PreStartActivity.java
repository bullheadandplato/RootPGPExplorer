package com.cryptopaths.cryptofm.startup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.startup.adapters.PagerAdapter;
import com.cryptopaths.cryptofm.utils.ActionHandler;

public class PreStartActivity extends AppCompatActivity {
    //app compat activity can also act as FragmentActivity

    ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_start);
        viewpager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter padapter = new PagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(padapter);
    }

    @ActionHandler(layoutResource = R.id.button)
    Button button=(Button)findViewById(R.id.button);

    button.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            // Perform action on click
        }
    });
}



package com.cryptopaths.cryptofm.startup;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

    @ActionHandler(layoutResource = R.id.pre_start_skip_button)
    public void onSkipButtonClick(View v){
        //start the intent for the password activity
    }

}



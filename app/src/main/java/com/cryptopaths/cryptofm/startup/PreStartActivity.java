package com.cryptopaths.cryptofm.startup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.startup.adapters.PagerAdapter;
import com.cryptopaths.cryptofm.utils.ActionHandler;

public class PreStartActivity extends AppCompatActivity {
    //app compat activity can also act as FragmentActivity




    ViewPager viewPager;
    PagerAdapter pAdapter;
    RadioButton radioButton1, radioButton2, radioButton3, radioButton4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_start);

        //initialize UI elements
        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
        radioButton4 = (RadioButton) findViewById(R.id.radioButton4);

        //set ViewPager adapter
        viewPager = (ViewPager) findViewById(R.id.pager);
        pAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pAdapter);
        pAdapter.setPreStartActivity(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                checkRadioButton(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @ActionHandler(layoutResource = R.id.pre_start_skip_button)
    public void onSkipButtonClick(View v){
        //start the intent for the password activity
        Intent intent=new Intent(this,InitActivity.class);
        //start the activity but not let the user get back to this activity
        startActivityForResult(intent,1);
        finish();
    }

    public void checkRadioButton(int num) {
        switch (num) {
            case 0:
                radioButton1.setChecked(true);
                break;
            case 1:
                radioButton2.setChecked(true);
                break;
            case 2:
                radioButton3.setChecked(true);
                break;
            case 3:
                radioButton4.setChecked(true);
                ((Button)findViewById(R.id.pre_start_skip_button)).setText("Finish >");
                break;

        }
    }

}



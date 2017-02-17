/*
 * Copyright (c) 2017. Osama Bin Omar
 *    This file is part of Crypto File Manager also known as Crypto FM
 *
 *     Crypto File Manager is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Crypto File Manager is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Crypto File Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.osama.cryptofm.startup;

/**
 * Created by Shadow on 1/12/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.osama.cryptofm.R;
import com.osama.cryptofm.startup.adapters.PagerAdapter;
import com.osama.cryptofm.utils.ActionHandler;

public class PreStartActivity extends AppCompatActivity {
    //app compat activity can also act as FragmentActivity




    ViewPager   viewPager;
    PagerAdapter pAdapter;
    RadioButton radioButton1, radioButton2, radioButton3, radioButton4;
    RelativeLayout prestartlayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_start);

        //initialize UI elements
        radioButton1    = (RadioButton)  findViewById(R.id.radioButton1);
        radioButton2    = (RadioButton)  findViewById(R.id.radioButton2);
        radioButton3    = (RadioButton)  findViewById(R.id.radioButton3);
        radioButton4    = (RadioButton)  findViewById(R.id.radioButton4);
        prestartlayout  =(RelativeLayout)findViewById(R.id.pre_start);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.pagercolor1));

        //set ViewPager adapter
        viewPager   = (ViewPager) findViewById(R.id.pager);
        pAdapter    = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pAdapter);
        pAdapter.setPreStartActivity(this);
        radioButton1.setChecked(true);
        prestartlayout.setBackgroundColor(getResources().getColor(R.color.pagercolor1));

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
                prestartlayout.setBackgroundColor(ContextCompat.getColor(this,R.color.pagercolor1));
                getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.pagercolor1));
                getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.pagercolor1));
                break;
            case 1:

                radioButton2.setChecked(true);
                prestartlayout.setBackgroundColor(ContextCompat.getColor(this,R.color.pagercolor2));
                getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.pagercolor2));
                getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.pagercolor2));
                break;
            case 2:
                radioButton3.setChecked(true);
                prestartlayout.setBackgroundColor(ContextCompat.getColor(this,R.color.pagercolor3));
                getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.pagercolor3));
                getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.pagercolor3));
                break;
            case 3:
                radioButton4.setChecked(true);
                prestartlayout.setBackgroundColor(ContextCompat.getColor(this,R.color.colorAccent));
                getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.colorAccent));
                getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorAccent));
                animateButtonAndShow();
                break;

        }
    }
    private void animateButtonAndShow(){
        ImageButton button= (ImageButton) findViewById(R.id.pre_start_skip_button);
        button.setVisibility(View.VISIBLE);
        //animate
        Animation animation= AnimationUtils.loadAnimation(this,R.anim.done_button_anim);
        button.startAnimation(animation);
    }

}



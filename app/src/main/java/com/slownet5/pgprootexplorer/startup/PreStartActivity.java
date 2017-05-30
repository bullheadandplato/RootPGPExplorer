/*
 * Copyright (c) 2017. slownet5
 *  This file is part of RootPGPExplorer also known as CryptoFM
 *
 *       RootPGPExplorer a is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       RootPGPExplorer is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with RootPGPExplorer.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.slownet5.pgprootexplorer.startup;


import android.content.Intent;
import android.os.Build;
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

import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.startup.adapters.PagerAdapter;
import com.slownet5.pgprootexplorer.utils.ActionHandler;

import me.relex.circleindicator.CircleIndicator;

public class PreStartActivity extends AppCompatActivity {
    private RelativeLayout  preStartLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_start);

        preStartLayout =(RelativeLayout)findViewById(R.id.pre_start);
        setColors(ContextCompat.getColor(this,R.color.colorAccent));

        //set ViewPager adapter
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        //set pager indicator
        CircleIndicator indicator = (CircleIndicator)findViewById(R.id.indicator);
        viewPager.setAdapter(pagerAdapter);
        indicator.setViewPager(viewPager);
        changePage(0);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changePage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        findViewById(R.id.pre_start_skip_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSkipButtonClick(v);
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

    public void changePage(int num) {
        switch (num) {
            case 0:
                preStartLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.colorAccent));
                setColors(ContextCompat.getColor(this,R.color.colorAccent));
                break;
            case 1:
                preStartLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.pagercolor2));
                setColors(ContextCompat.getColor(this,R.color.pagercolor2));
                setColors(ContextCompat.getColor(this,R.color.pagercolor2));
                break;
            case 2:
                preStartLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.pagercolor3));
                setColors(ContextCompat.getColor(this,R.color.pagercolor3));
                setColors(ContextCompat.getColor(this,R.color.pagercolor3));
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
    private void setColors(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(color);
            getWindow().setStatusBarColor(color);
        }
    }

}



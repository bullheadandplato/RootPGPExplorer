package com.cryptopaths.cryptofm.filemanager.ui;

import android.support.design.widget.TabLayout;
import android.util.Log;

/**
 * Created by tripleheader on 1/22/17.
 */

public class TabChangedListener implements TabLayout.OnTabSelectedListener {
    private static final String TAG=TabChangedListener.class.getName();

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if(tab.getPosition()==1){
                Log.d(TAG, "onTabSelected: tab two select");
            }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}

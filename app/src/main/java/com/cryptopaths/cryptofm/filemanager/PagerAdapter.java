package com.cryptopaths.cryptofm.filemanager;

/**
 * Created by Shadow on 1/21/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.cryptopaths.cryptofm.filemanager.ui.TabsFragmentOne;
import com.cryptopaths.cryptofm.filemanager.ui.TabsFragmentTwo;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabsFragmentOne tab1 = new TabsFragmentOne();
                return tab1;
            case 1:
                TabsFragmentTwo tab2 = new TabsFragmentTwo();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}



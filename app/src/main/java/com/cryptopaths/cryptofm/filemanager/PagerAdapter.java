package com.cryptopaths.cryptofm.filemanager;

/**
 * Created by Shadow on 1/21/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.cryptopaths.cryptofm.filemanager.ui.TabsFragmentOne;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private String tabTitles[] = new String[] { "Home", "Sdcard" };


    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        return TabsFragmentOne.newInstance(SharedData.FILES_ROOT_DIRECTORY);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}



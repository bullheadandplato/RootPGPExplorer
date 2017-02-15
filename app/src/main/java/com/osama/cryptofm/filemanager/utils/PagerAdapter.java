package com.osama.cryptofm.filemanager.utils;

/**
 * Created by Shadow on 1/21/2017.
 *
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.osama.cryptofm.filemanager.ui.TabsFragmentOne;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private String              tabTitles[] = new String[] { "Home", "Sdcard" };
    private TabsFragmentOne[]   tabsFragment;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        tabsFragment=new TabsFragmentOne[mNumOfTabs];
    }

    @Override
    public Fragment getItem(int position) {
        tabsFragment[position] =TabsFragmentOne.newInstance(tabTitles[position],position);
        return tabsFragment[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(SharedData.EXTERNAL_SDCARD_ROOT_PATH==null){
            return null;
        }
        return "Storage "+position;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public void setTitles(String[] mStorageTitles) {
        tabTitles=mStorageTitles;
    }
}



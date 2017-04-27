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

package com.osama.cryptofmroot.filemanager.utils;

/**
 * Created by Shadow on 1/21/2017.
 *
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.osama.cryptofmroot.filemanager.ui.TabsFragmentOne;

import java.util.ArrayList;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private ArrayList<String> tabPaths;
    private TabsFragmentOne[]   tabsFragment;

    public PagerAdapter(FragmentManager fm, int NumOfTabs,ArrayList<String> paths) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        tabsFragment=new TabsFragmentOne[mNumOfTabs];
        this.tabPaths=paths;
    }

    @Override
    public Fragment getItem(int position) {
        tabsFragment[position] =TabsFragmentOne.newInstance(tabPaths.get(position),position);
        return tabsFragment[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title=tabPaths.get(position);
        title=title.substring(0,title.lastIndexOf('/'));
        title=title.substring(title.lastIndexOf('/')+1,title.length());
        return title;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}



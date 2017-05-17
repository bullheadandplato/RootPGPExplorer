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

package com.slownet5.pgprootexplorer.filemanager.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.slownet5.pgprootexplorer.filemanager.ui.TabsFragmentOne;

import java.util.ArrayList;

public class TabsPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<String> tabPaths;
    private ArrayList<String> pageTitles;
    private TabsFragmentOne[]   tabsFragment;

    public TabsPagerAdapter(FragmentManager fm, ArrayList<String> paths, ArrayList<String> pageTitles) {
        super(fm);
        tabsFragment=new TabsFragmentOne[paths.size()];
        this.tabPaths=paths;
        this.pageTitles=pageTitles;
    }

    @Override
    public Fragment getItem(int position) {
        tabsFragment[position] =TabsFragmentOne.newInstance(tabPaths.get(position),position);
        return tabsFragment[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles.get(position);
    }

    @Override
    public int getCount() {
        return tabPaths.size();
    }

}



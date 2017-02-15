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

package com.osama.cryptofm.startup.adapters;

/**
 * Created by Shadow on 1/12/2017.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.osama.cryptofm.startup.PreStartActivity;
import com.osama.cryptofm.startup.fragments.ViewPagerFragmentFour;
import com.osama.cryptofm.startup.fragments.ViewPagerFragmentOne;
import com.osama.cryptofm.startup.fragments.ViewPagerFragmentThree;
import com.osama.cryptofm.startup.fragments.ViewPagerFragmentTwo;

public class PagerAdapter extends FragmentPagerAdapter {


    private PreStartActivity mActivity;


    public void setPreStartActivity(PreStartActivity act){
        this.mActivity=act;
    }

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int arg0) {
        // TODO Auto-generated method stub
        switch (arg0) {
            case 0:
                return new ViewPagerFragmentOne();
            case 1:
                return new ViewPagerFragmentTwo();

            case 2:
                return new ViewPagerFragmentThree();
            case 3:
                return new ViewPagerFragmentFour();

            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 4;
    }


}


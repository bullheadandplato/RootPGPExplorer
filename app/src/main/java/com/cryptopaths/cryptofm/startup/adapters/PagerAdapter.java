package com.cryptopaths.cryptofm.startup.adapters;

/**
 * Created by Shadow on 1/12/2017.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cryptopaths.cryptofm.startup.fragments.ViewPagerFragmentFour;
import com.cryptopaths.cryptofm.startup.fragments.ViewPagerFragmentOne;
import com.cryptopaths.cryptofm.startup.fragments.ViewPagerFragmentThree;
import com.cryptopaths.cryptofm.startup.fragments.ViewPagerFragmentTwo;

public class PagerAdapter extends FragmentPagerAdapter {


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


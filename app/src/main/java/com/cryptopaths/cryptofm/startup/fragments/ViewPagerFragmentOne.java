package com.cryptopaths.cryptofm.startup.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptopaths.cryptofm.R;

/**
 * Created by Shadow on 1/12/2017.
 */

public class ViewPagerFragmentOne  extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pager_fragment_one,container,false);

    }
}

package com.cryptopaths.cryptofm.filemanager.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptopaths.cryptofm.R;

/**
 * Created by Shadow on 1/21/2017.
 */

public class TabsFragmentTwo extends Fragment {
    private static final String TAG=TabsFragmentTwo.class.getName();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: only called once");
        return inflater.inflate(R.layout.tabs_fragment_two,container,false);

    }
}

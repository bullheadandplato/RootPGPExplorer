package com.cryptopaths.cryptofm.filemanager.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.filemanager.FragmentCallbacks;

/**
 * Created by Shadow on 1/21/2017.
 */

public class TabsFragmentOne extends Fragment {
    private static final String TAG=TabsFragmentOne.class.getName();
    private static boolean  ALREADY_CALLED=false;
    private FragmentCallbacks mCallbacks;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: view only created once");
        if(mCallbacks==null){
            Log.d(TAG, "onCreateView: yes saved instance is not null");
        }
        return inflater.inflate(R.layout.tabs_fragment_one,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: view created");
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: fragment activity created");
        super.onActivityCreated(savedInstanceState);
        mCallbacks=(FragmentCallbacks)getActivity();
        if(!ALREADY_CALLED){
            mCallbacks.init();
            ALREADY_CALLED=true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("saving","google");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        mCallbacks.finishActionMode();
        super.onPause();
    }
}

package com.cryptopaths.cryptofm.startup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptopaths.cryptofm.R;

/**
 * Created by tripleheader on 11/28/16.
 */

public class InitActivityThirdFragment extends Fragment {
    public interface FragmentCreated{
        public void onThirdFragmentCreated();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("fragment","creating third fragment");
        return inflater.inflate(R.layout.second_fragment,container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentCreated fragmentCreated = (FragmentCreated) getActivity();
        fragmentCreated.onThirdFragmentCreated();
    }
}

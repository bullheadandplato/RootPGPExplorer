package com.osama.cryptofm.startup.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osama.cryptofm.R;

/**
 * Created by tripleheader on 12/23/16.
 * Shows when user choose different password to database
 */

public class PasswordsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.passwords_fragment_layout,container,false);
    }
}

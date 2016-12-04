package com.cryptopaths.cryptofm;

import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tripleheader on 11/28/16.
 *
 */

public class ThirdFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.third_fragment,container,false);
        ChooseDirAdapter adapter=new ChooseDirAdapter(getActivity());
        ListView listView=(ListView)rootView.findViewById(R.id.choose_dir_list);
        listView.setAdapter(adapter);
        return rootView;
    }

}

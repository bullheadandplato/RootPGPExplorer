package com.cryptopaths.cryptofm.startup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cryptopaths.cryptofm.R;

import java.util.ArrayList;

/**
 * Created by tripleheader on 11/28/16.
 *
 */

public class SecondFragment extends Fragment {
    private ChooseDirAdapter     mAdapter;

    private ArrayList<String>    allSelectedPositions  = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView           = inflater.inflate(R.layout.third_fragment,container,false);
        final ListView listView = (ListView)rootView.findViewById(R.id.choose_dir_list);
        View footerView         = inflater.inflate(R.layout.choose_file_footer,container,false);
        mAdapter                = new ChooseDirAdapter(getActivity());

        listView.setAdapter(mAdapter);
        listView.addFooterView(footerView);
        // set item selected in filebrowse_lisrview by just a Tap
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!isAlreadySelected(i)){
                    allSelectedPositions.add("value"+i);

                }else{
                    allSelectedPositions.remove("value"+i);

                }
                mAdapter.setAllSelectedPositions(allSelectedPositions);
                mAdapter.notifyDataSetChanged();

            }
        });
        return rootView;
    }
    private boolean isAlreadySelected(int position){
        for (String pos:
             allSelectedPositions) {
            if(pos.equals("value"+position)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getAllSelectedPositions() {
        return mAdapter.getmSelectedFoldersPaths();
    }
}

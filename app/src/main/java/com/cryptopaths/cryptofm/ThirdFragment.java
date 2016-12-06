package com.cryptopaths.cryptofm;

import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tripleheader on 11/28/16.
 *
 */

public class ThirdFragment extends Fragment {
    private ArrayList<String> allSelectedPositions=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.third_fragment,container,false);
        final ChooseDirAdapter adapter=new ChooseDirAdapter(getActivity());
        final ListView listView=(ListView)rootView.findViewById(R.id.choose_dir_list);
        listView.setAdapter(adapter);
        // set item selected in listview by just a Tap
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!isAlreadySelected(i)){
                    allSelectedPositions.add("value"+i);

                }else{
                    allSelectedPositions.remove("value"+i);

                }
                adapter.setAllSelectedPositions(allSelectedPositions);
                adapter.notifyDataSetChanged();

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

}

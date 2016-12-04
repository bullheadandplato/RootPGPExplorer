package com.cryptopaths.cryptofm;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tripleheader on 12/4/16.
 */

public class ChooseDirAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<String> mDataset;
    public ChooseDirAdapter(Context context){
        mInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDataset=getAllDirs();
    }
    @Override
    public int getCount() {
        return mDataset.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder=new ViewHolder();
        if(view==null){
            view=mInflater.inflate(R.layout.choose_dir_listview,null);
        }
        viewHolder.checkBox=(CheckBox)view.findViewById(R.id.checkBox);
        viewHolder.checkBox.setText(mDataset.get(i));
        return view;
    }
    private class ViewHolder{
        public CheckBox checkBox;
    }
    private List<String> getAllDirs(){
        List<String> tempList=new ArrayList<>();
        File file=new File( Environment.getExternalStorageDirectory().getPath());
        File[] files=file.listFiles();
        for (File f:
                files) {
            tempList.add(f.getName());
        }
        return tempList;
    }
}

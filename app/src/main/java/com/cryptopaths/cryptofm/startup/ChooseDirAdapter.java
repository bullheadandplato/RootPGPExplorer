package com.cryptopaths.cryptofm.startup;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptopaths.cryptofm.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tripleheader on 12/4/16.
 */

public class ChooseDirAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<String> allSelectedPositions=new ArrayList<>();
    private ArrayList<String> mSelectedFoldersPaths=new ArrayList<>();
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
        viewHolder.textView=(TextView) view.findViewById(R.id.folder_name_textview);
        viewHolder.textView.setText(mDataset.get(i));
        //change image according to user selection
        viewHolder.imageView=(ImageView)view.findViewById(R.id.folder_icon_image);
        if(isSelected(i)){
            viewHolder.imageView.setImageResource(R.drawable.ic_check_circle_white_48dp);
            if(!isSelectedFolder(viewHolder.textView.getText())){
                mSelectedFoldersPaths.add(viewHolder.textView.getText().toString());

            }

        }else{
            if(isSelectedFolder(viewHolder.textView.getText())){
                mSelectedFoldersPaths.remove(viewHolder.textView.getText().toString());
            }
            viewHolder.imageView.setImageResource(R.drawable.ic_folder_white_48dp);
        }
        return view;
    }

    private boolean isSelectedFolder(CharSequence text) {
        for (String s:
             mSelectedFoldersPaths) {
            if(s.equals(text)){
                return true;
            }
        }
        return false;
    }


    private class ViewHolder{
        public TextView textView;
        public ImageView imageView;
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

    public void setAllSelectedPositions(ArrayList<String> allSelectedPositions) {
        this.allSelectedPositions = allSelectedPositions;
    }
    private boolean isSelected(int position){
        for (String pos:
                allSelectedPositions) {
            if(pos.equals("value"+position)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getmSelectedFoldersPaths() {
        return mSelectedFoldersPaths;
    }
}

/*
 * Copyright (c) 2017. slownet5
 *  This file is part of RootPGPExplorer also known as CryptoFM
 *
 *       RootPGPExplorer a is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       RootPGPExplorer is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with RootPGPExplorer.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.slownet5.pgprootexplorer.filemanager.listview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.filemanager.utils.SharedData;
import com.slownet5.pgprootexplorer.filemanager.utils.TaskHandler;


/**
 * Created by tripleheader on 12/14/16.
 *
 *
 */

public class FileListAdapter extends RecyclerView.Adapter<ViewHolder>{

    private  static final String    TAG                 = "filesList";
    private static final int        NORMAL_VIEW         = 50;
    private Context mContext;
    private FileFillerWrapper mFileFiller;
    private FileSelectionManagement mManager;
    private TaskHandler mTaskHandler;


    public FileListAdapter(Context context){
            this.mContext=context;

    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context=parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
        View view;
            if(SharedData.LINEAR_LAYOUTMANAGER){
                view=inflater.inflate(R.layout.filemanager_listview_item,parent,false);
            }else{
                view = inflater.inflate(R.layout.filebrowse_card_view,parent,false);
            }
            return new ViewHolder(view,mContext,mManager,mFileFiller,mTaskHandler);

    }

    public void setmFileFiller(FileFillerWrapper mFileFiller) {
        this.mFileFiller = mFileFiller;
        mManager=new FileSelectionManagement(mContext,this);
    }

    public void setmTaskHandler(TaskHandler mTaskHandler) {
        this.mTaskHandler = mTaskHandler;
    }

    public FileSelectionManagement getmManager() {
        return mManager;
    }

    public FileFillerWrapper getmFileFiller() {
        return mFileFiller;
    }

    @Override
    public int getItemViewType(int position) {
            return NORMAL_VIEW;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            DataModelFiles mDataModel= mFileFiller.getFileAtPosition(position);
            TextView textView1=holder.mTextView;
            ImageView imageView=holder.mImageView;
            TextView textView2=holder.mFolderSizeTextView;
        ImageView imageView1=holder.mGridSmallIcon;

            TextView textView4=holder.mNumberFilesTextView;

                textView1.setText(mDataModel.getFileName());
                textView2.setText(mDataModel.getFileDate());
                textView4.setText(mDataModel.getFileExtension());
                imageView.setImageDrawable(mDataModel.getFileIcon());
        if(!SharedData.LINEAR_LAYOUTMANAGER){
            imageView1.setImageDrawable(mDataModel.getFileIcon());
        }
        holder.itemView.setBackgroundColor(mDataModel.getBackgroundColor());

    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mFileFiller.getTotalFilesCount();
    }

}

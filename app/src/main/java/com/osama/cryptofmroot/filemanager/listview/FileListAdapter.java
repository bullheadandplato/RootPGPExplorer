/*
 * Copyright (c) 2017. Osama Bin Omar
 *    This file is part of Crypto File Manager also known as Crypto FM
 *
 *     Crypto File Manager is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Crypto File Manager is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Crypto File Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.osama.cryptofmroot.filemanager.listview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.osama.cryptofmroot.R;


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

    public FileListAdapter(Context context){
            this.mContext=context;

    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context=parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view               = inflater.inflate(R.layout.filebrowse_card_view,parent,false);
            return new ViewHolder(view,mContext,mManager,mFileFiller);

    }

    public void setmFileFiller(FileFillerWrapper mFileFiller) {
        this.mFileFiller = mFileFiller;
        mManager=new FileSelectionManagement(mContext,this);
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
        Log.d(TAG, "onBindViewHolder: loading files");
             DataModelFiles mDataModel= mFileFiller.getFileAtPosition(position);
            TextView textView1=holder.mTextView;
            ImageView imageView=holder.mImageView;
            TextView textView2=holder.mFolderSizeTextView;
            ImageView image=holder.mEncryptionStatusImage;
            TextView textView4=holder.mNumberFilesTextView;

            textView1.setText(mDataModel.getFileName());
            textView2.setText(mDataModel.getFileSize());
            image.setImageDrawable(mDataModel.getFileEncryptionStatus());
            textView4.setText(mDataModel.getFileExtension());
            imageView.setImageDrawable(mDataModel.getFileIcon());

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

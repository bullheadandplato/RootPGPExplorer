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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.osama.cryptofmroot.CryptoFM;
import com.osama.cryptofmroot.R;
import com.osama.cryptofmroot.filemanager.utils.SharedData;
import com.osama.cryptofmroot.root.RootUtils;
import com.osama.cryptofmroot.utils.FileUtils;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by tripleheader on 1/21/17
 * View holder
 */

class ViewHolder extends RecyclerView.ViewHolder{
    private FileSelectionManagement mFileSelectionManagement;
    private FileFillerWrapper mFileFiller;
    private static final String TAG="ViewHolder";
    ImageView        mImageView;
    TextView         mTextView;
    TextView         mNumberFilesTextView;
    TextView         mFolderSizeTextView;


    ViewHolder(View itemView, Context c,FileSelectionManagement m,FileFillerWrapper wrapper){
        super(itemView);
        mFileSelectionManagement= m;
        mFileFiller=wrapper;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(SharedData.SELECTION_MODE){
                    mFileSelectionManagement.selectionOperation(getAdapterPosition());
                    return;
                }
                TextView textView   = (TextView)view.findViewById(R.id.list_textview);
                String filename1     = textView.getText().toString();
                String completeFilename=mFileFiller.getCurrentPath()+filename1;

                if(mFileFiller.getFileAtPosition(getAdapterPosition()).getFile()){
                    if(SharedData.STARTED_IN_SELECTION_MODE){
                        Log.d(TAG, "onClick: yes nigga im started in selection mode");
                        mFileSelectionManagement.selectFileInSelectionMode(getAdapterPosition());
                        return;
                    }

                    mFileSelectionManagement.openFile(completeFilename);
                }else{
                        mFileSelectionManagement.openFolder(completeFilename+"/",getAdapterPosition());
                    }

            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(SharedData.STARTED_IN_SELECTION_MODE){
                    return false;
                }
                else if(!SharedData.SELECTION_MODE && !SharedData.IS_IN_COPY_MODE) {
                    Log.d(TAG, "onLongClick: action mode is not being displayed");
                    mFileSelectionManagement.startSelectionMode();
                }
                if(!SharedData.IS_IN_COPY_MODE){
                    mFileSelectionManagement.selectionOperation(getAdapterPosition());
                }
                return true;

            }
        });

        mTextView               = (TextView)itemView.findViewById(R.id.list_textview);
        mImageView              = (ImageView)itemView.findViewById(R.id.list_imageview);
        mNumberFilesTextView    = (TextView)itemView.findViewById(R.id.nofiles_textview);
        mFolderSizeTextView     = (TextView)itemView.findViewById(R.id.folder_size_textview);

    }

}

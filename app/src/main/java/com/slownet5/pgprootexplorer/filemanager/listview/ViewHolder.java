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
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.filemanager.utils.SharedData;
import com.slownet5.pgprootexplorer.filemanager.utils.TaskHandler;
import com.slownet5.pgprootexplorer.filemanager.utils.TaskHandlerWrapper;

import java.util.ArrayList;

/**
 * Created by tripleheader on 1/21/17
 * View holder
 */

class ViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener{
    private FileSelectionManagement mFileSelectionManagement;
    private FileFillerWrapper mFileFiller;
    private TaskHandlerWrapper mTaskHandler;
    private Context context;

    private static final String TAG="ViewHolder";
    ImageView        mImageView;
    TextView         mTextView;
    TextView         mNumberFilesTextView;
    TextView         mFolderSizeTextView;
    ImageView           mGridSmallIcon;


    ViewHolder(final View itemView, Context c, FileSelectionManagement m, FileFillerWrapper wrapper,TaskHandlerWrapper mTaskHandler){
        super(itemView);
        mFileSelectionManagement= m;
        mFileFiller=wrapper;
        this.mTaskHandler=mTaskHandler;
        this.context=c;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(SharedData.SELECTION_MODE){
                    mFileSelectionManagement.selectionOperation(getAdapterPosition(),view);
                    return;
                }
                TextView textView;
                if(SharedData.LINEAR_LAYOUTMANAGER){
                    textView   = (TextView)view.findViewById(R.id.list_textview);
                }else{
                    textView   = (TextView)view.findViewById(R.id.grid_textview);
                }
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
                        mFileSelectionManagement.openFolder(completeFilename+"/",getAdapterPosition(),view);
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
                    mFileSelectionManagement.selectionOperation(getAdapterPosition(),view);
                }
                return true;

            }
        });
        if(SharedData.LINEAR_LAYOUTMANAGER) {
            mTextView = (TextView) itemView.findViewById(R.id.list_textview);
            mImageView = (ImageView) itemView.findViewById(R.id.list_imageview);
            mNumberFilesTextView = (TextView) itemView.findViewById(R.id.nofiles_textview);
            mFolderSizeTextView = (TextView) itemView.findViewById(R.id.folder_size_textview);
        }else{
            mTextView = (TextView) itemView.findViewById(R.id.grid_textview);
            mImageView = (ImageView) itemView.findViewById(R.id.grid_imageview);
            mNumberFilesTextView = (TextView) itemView.findViewById(R.id.gridnofiles_textview);
            mFolderSizeTextView = (TextView) itemView.findViewById(R.id.gridfolder_size_textview);
            mGridSmallIcon=(ImageView)itemView.findViewById(R.id.grid_imageview2);
            itemView.findViewById(R.id.grid_menu_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPopUpMenu(v);
                        }
                    });
        }
    }

    private void showPopUpMenu(View v){
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.grid_actions, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (mTaskHandler==null){
            Toast.makeText(context,"Oops caught an error in executing this operation.",Toast.LENGTH_LONG).show();
            return false;
        }
        String filePath=mFileFiller.getCurrentPath()+mFileFiller.getFileAtPosition(getAdapterPosition()).getFileName();
        ArrayList<String> tmp=new ArrayList<>();
        tmp.add(filePath);
        switch (item.getItemId()){
            case R.id.grid_menu_encrypt_item: mTaskHandler.encryptTask(tmp); break;
            case R.id.grid_menu_decrypt_item: mTaskHandler.decryptFile(SharedData.USERNAME,SharedData.KEY_PASSWORD,SharedData.DB_PASSWORD,tmp);
        }
        return true;
    }
}

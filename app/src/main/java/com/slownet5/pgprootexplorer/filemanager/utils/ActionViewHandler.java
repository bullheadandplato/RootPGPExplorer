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

package com.slownet5.pgprootexplorer.filemanager.utils;

import android.content.Context;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.filemanager.listview.FileSelectionManagement;
import com.slownet5.pgprootexplorer.filemanager.ui.FileManagerActivity;

import java.util.ArrayList;


/**
 * Created by tripleheader on 1/13/17.
 * Action mode callbacks
 */

public class ActionViewHandler implements ActionMode.Callback {
    private Context mContext;
    private FileSelectionManagement mManager;
    private TaskHandler mTaskHandler;
    public ActionViewHandler(Context context,FileSelectionManagement m,TaskHandler taskHandler){
        this.mContext=context;
        this.mManager=m;
        this.mTaskHandler=taskHandler;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        Log.d("actionMode", "onCreateActionMode: created action mode");
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.file_select_options,menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {


        if (item.getItemId()==R.id.rename_menu_item){
            mTaskHandler.renameFile();
        }
        else if(item.getItemId()==R.id.delete_menu_item){
            mTaskHandler.deleteFile(new ArrayList<String>(mManager.getmSelectedFilePaths()));

        }
        else if(item.getItemId()==R.id.encrypt_menu_item){
            mTaskHandler.encryptTask(mManager.getmSelectedFilePaths());
        }
        else if(item.getItemId()==R.id.decrypt_menu_item){
             mTaskHandler.decryptFile(
                                    SharedData.USERNAME,
                                    SharedData.KEY_PASSWORD,
                                    SharedData.DB_PASSWORD,
                     new ArrayList<String>(mManager.getmSelectedFilePaths())

             );
        }
        else if(item.getItemId()==R.id.selectall_menu_item){
            mManager.selectAllFiles();
        }
        else if(item.getItemId()==R.id.move_menu_item){
            Log.d("move", "onActionItemClicked: moving files");
            SharedData.IS_IN_COPY_MODE=true;
            SharedData.IS_COPYING_NOT_MOVING=false;
            //set the files to be move or copied
            mTaskHandler.setmSelectedFiles(new ArrayList<String>(mManager.getmSelectedFilePaths()));
            ((FileManagerActivity)mContext).showCopyDialog();
        }else if(item.getItemId()==R.id.copy_menu_item){
            SharedData.IS_IN_COPY_MODE=true;
            SharedData.IS_COPYING_NOT_MOVING=true;
            mTaskHandler.setmSelectedFiles(new ArrayList<String>(mManager.getmSelectedFilePaths()));
            ((FileManagerActivity)mContext).showCopyDialog();

        }else if(item.getItemId()==R.id.openwith_menu_item){
            UiUtils.openWith(mManager.getmSelectedFilePaths().get(0),mContext);
        }else if(item.getItemId()==R.id.compress_menu_item){
            mTaskHandler.compressTask(mManager.getmSelectedFilePaths(),false);
        }else if(item.getItemId()==R.id.share_menu_item){
            UiUtils.shareFile(mManager.getmSelectedFilePaths().get(0),mContext);
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        Log.d("action","destroying action mode");
        finishFileSelection();
    }
    private void finishFileSelection(){
        SharedData.SELECT_COUNT=0;
        SharedData.SELECTION_MODE=false;
        SharedData.IS_OPENWITH_SHOWN=false;
        //mManager.resetFilesProperties();
        mManager.setmSelectionMode(false);
        UiUtils.refill(mManager.getmFileListAdapter());
    }
}

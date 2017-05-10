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

package com.osama.cryptofmroot.filemanager.utils;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.osama.cryptofmroot.R;
import com.osama.cryptofmroot.filemanager.listview.FileSelectionManagement;
import com.osama.cryptofmroot.filemanager.ui.FileManagerActivity;

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
            finishFileSelection();
        }
        else if(item.getItemId()==R.id.delete_menu_item){
            finishFileSelection();
            mTaskHandler.deleteFile();

        }
        else if(item.getItemId()==R.id.encrypt_menu_item){
           finishFileSelection();
            mTaskHandler.encryptTask(mManager.getmSelectedFilePaths());

        }
        else if(item.getItemId()==R.id.decrypt_menu_item){
            finishFileSelection();
            if(SharedData.KEY_PASSWORD==null) {
                final Dialog dialog = new Dialog(mContext);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.password_dialog_layout);
                dialog.show();
                dialog.findViewById(R.id.cancel_decrypt_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                final EditText editText = (EditText) dialog.findViewById(R.id.key_password);
                dialog.findViewById(R.id.decrypt_file_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (editText.getText().length() < 1) {
                            editText.setError("please give me your encryption password");
                            return;
                        } else {
                            SharedData.KEY_PASSWORD = editText.getText().toString();
                            dialog.dismiss();
                            mTaskHandler.decryptFile(
                                    SharedData.USERNAME,
                                    SharedData.KEY_PASSWORD,
                                    SharedData.DB_PASSWORD,
                                    mManager.getmSelectedFilePaths()
                                    );
                        }
                    }
                });
            }else{
                mTaskHandler.decryptFile(
                        SharedData.USERNAME,
                        SharedData.KEY_PASSWORD,
                        SharedData.DB_PASSWORD,
                        mManager.getmSelectedFilePaths()
                );
            }
        }
        else if(item.getItemId()==R.id.selectall_menu_item){
            mManager.selectAllFiles();
        }
        else if(item.getItemId()==R.id.move_menu_item){
            Log.d("move", "onActionItemClicked: moving files");
            SharedData.IS_IN_COPY_MODE=true;
            SharedData.IS_COPYING_NOT_MOVING=false;
            //set the files to be move or copied
            mTaskHandler.setmSelectedFiles((ArrayList<String>) mManager.getmSelectedFilePaths().clone());
            ((FileManagerActivity)mContext).showCopyDialog();
        }else if(item.getItemId()==R.id.copy_menu_item){
            SharedData.IS_IN_COPY_MODE=true;
            SharedData.IS_COPYING_NOT_MOVING=true;
            mTaskHandler.setmSelectedFiles((ArrayList<String>) mManager.getmSelectedFilePaths().clone());
            ((FileManagerActivity)mContext).showCopyDialog();

        }else if(item.getItemId()==R.id.openwith_menu_item){
           finishFileSelection();
            mTaskHandler.openWith();

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
        if(!SharedData.DO_NOT_RESET_ICON){
            mManager.resetFileIcons();
        }
        mManager.setmSelectionMode(false);
    }

    /**
     * end of action mode section
     */
}

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

package com.osama.cryptofm.filemanager.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.osama.cryptofm.R;
import com.osama.cryptofm.filemanager.listview.FileListAdapter;
import com.osama.cryptofm.filemanager.listview.FileSelectionManagement;
import com.osama.cryptofm.startup.OptionActivity;
import com.osama.cryptofm.tasks.DecryptTask;
import com.osama.cryptofm.tasks.DeleteTask;
import com.osama.cryptofm.tasks.EncryptTask;
import com.osama.cryptofm.tasks.MoveTask;
import com.osama.cryptofm.tasks.RenameTask;

import java.util.ArrayList;

/**
 * Created by tripleheader on 1/13/17.
 * task executor
 */

public class TaskHandler {
    private EncryptTask             mEncryptTask;
    private DecryptTask             mDecryptTask;
    private FileListAdapter         mAdapter;
    private Context                 mContext;
    private FileSelectionManagement mFileSelectionManagement;
    private ArrayList<String>       mSelectedFiles;
    public TaskHandler(Context context,FileListAdapter adapter,FileSelectionManagement m){
        this.mContext=context;
        mAdapter=adapter;
        mFileSelectionManagement=m;
    }

    public void renameFile(){
        final Dialog dialog = UiUtils.createDialog(
                mContext,
                "Rename file",
                "rename"
        );

        final EditText folderEditText = (EditText)dialog.findViewById(R.id.foldername_edittext);
        Button okayButton			  = (Button)dialog.findViewById(R.id.create_file_button);
        String currentFileName		  = mFileSelectionManagement.getmSelectedFilePaths().get(0);

        currentFileName = currentFileName.substring(
                currentFileName.lastIndexOf('/')+1,
                currentFileName.length()
        );
        folderEditText.setText(currentFileName);

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String folderName=folderEditText.getText().toString();
                if(folderName.length()<1){
                    folderEditText.setError("Give me the folder name");
                }else{
                    new RenameTask(
                            mContext,
                            mAdapter,
                            mFileSelectionManagement.getmSelectedFilePaths().get(0),
                            folderName
                    ).execute();
                    dialog.dismiss();
                }
            }
        });
    }
    public void deleteFile(){
        AlertDialog dialog=new AlertDialog.Builder(mContext).create();
        dialog.setTitle("Delete confirmation");
        dialog.setMessage("Do you really want to delete these files(s)?");
        dialog.setButton(
                DialogInterface.BUTTON_POSITIVE,
                "yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DeleteTask task=new DeleteTask(
                                mContext,
                                mAdapter,
                                (ArrayList<String>) mFileSelectionManagement.getmSelectedFilePaths().clone());
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    }
                });
        dialog.setButton(
                DialogInterface.BUTTON_NEUTRAL,
                "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //do nothing
                    }
                });
        dialog.show();
    }

    public void decryptFile(final String username, final String keypass, final String dbpass,ArrayList<String> files) {

        if(!SharedData.KEYS_GENERATED){
            //generate keys first
            generateKeys();
            return;
        }
        SharedData.IS_TASK_CANCELED=false;
        int size=files.size();
        for (int i = 0; i < size; i++) {
            if(SharedData.checkIfInRunningTask(files.get(i))){
                            Toast.makeText(
                    mContext,
                    "Another operation is already running on files, please wait",
                    Toast.LENGTH_LONG
            ).show();
            return;
            }
        }

        SharedData.CURRENT_RUNNING_OPERATIONS=files;

        mDecryptTask=new DecryptTask(
                mContext,
                mAdapter,
                (ArrayList<String>) files.clone(),
                dbpass,
                username,
                keypass
        );
        try{
            mDecryptTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }catch (IllegalStateException ex){
            Toast.makeText(
                    mContext,
                    "Already decrypting files",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
    public void encryptTask(ArrayList<String> files){
        //check if user hasn't generate keys

        if(!SharedData.KEYS_GENERATED){
            //generate keys first
            generateKeys();
            return;
        }
        SharedData.IS_TASK_CANCELED=false;
        int size=files.size();
        for (int i = 0; i < size; i++) {
            if(SharedData.checkIfInRunningTask(files.get(i))){
             Toast.makeText(
                    mContext,
                    "Another operation is already running on files, please wait",
                    Toast.LENGTH_LONG
            ).show();
            return ;
            }
        }
        SharedData.CURRENT_RUNNING_OPERATIONS=files;
        mEncryptTask=new EncryptTask(
                mContext,
                mAdapter,
                (ArrayList<String>) files.clone()
        );
        mEncryptTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void generateKeys() {
        //show user of what Im going to do
        final AlertDialog.Builder dialog=new AlertDialog.Builder(mContext);
        dialog.setCancelable(false);
        dialog.setTitle("Keys not generated");
        dialog.setMessage("Looks like you haven't generated your keys. You need to generate keys now");
        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                 //get the password
                Intent intent = new Intent(mContext, OptionActivity.class);
                mContext.startActivity(intent);
            }
        });
        dialog.show();


    }

    public DecryptTask getDecryptTask() {
        return mDecryptTask;
    }

    public EncryptTask getEncryptTask() {
        return mEncryptTask;
    }

    public void moveFiles(String dest,FileListAdapter m){
        //make sure files have been placed
        assert mSelectedFiles!=null;
        if(mSelectedFiles.size()<1){
            Log.d("MoveTask", "moveFiles: files are not added");
        }
        new MoveTask(mContext,mSelectedFiles,dest,m).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public ArrayList<String> getmSelectedFiles() {
        return mSelectedFiles;
    }

    public void setmSelectedFiles(ArrayList<String> mSelectedFiles) {
        this.mSelectedFiles = mSelectedFiles;
    }
}

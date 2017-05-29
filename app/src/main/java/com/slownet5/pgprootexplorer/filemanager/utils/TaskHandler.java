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

import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.filemanager.listview.FileListAdapter;
import com.slownet5.pgprootexplorer.filemanager.listview.FileSelectionManagement;
import com.slownet5.pgprootexplorer.startup.OptionActivity;
import com.slownet5.pgprootexplorer.tasks.CompressTask;
import com.slownet5.pgprootexplorer.tasks.DecryptTask;
import com.slownet5.pgprootexplorer.tasks.DeleteTask;
import com.slownet5.pgprootexplorer.tasks.EncryptTask;
import com.slownet5.pgprootexplorer.tasks.MoveTask;
import com.slownet5.pgprootexplorer.tasks.RenameTask;

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
    private static final String TAG=TaskHandler.class.getCanonicalName();
    public TaskHandler(Context context,FileListAdapter adapter,FileSelectionManagement m){
        this.mContext=context;
        mAdapter=adapter;
        mFileSelectionManagement=m;
    }

    public void renameFile(){
        if(!isOperationNotRunning(mFileSelectionManagement.getmSelectedFilePaths())){
            return;
        }
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
    public void deleteFile(final ArrayList<String> files){
        if(!isOperationNotRunning(files)){
            return;
        }
        final AlertDialog dialog=new AlertDialog.Builder(mContext).create();
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
                                files
                        );
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        dialog.dismiss();

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

    private boolean isCalled=false;
    public void decryptFile(final String username, final String keypass, final String dbpass, final ArrayList<String> files) {

        if (!SharedData.KEYS_GENERATED) {
            //generate keys first
            generateKeys();
            return;
        }
        if ((SharedData.KEY_PASSWORD == null || !SharedData.ASK_KEY_PASSS_CONFIG)&& !isCalled) {
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
                        isCalled=true;
                        SharedData.KEY_PASSWORD = editText.getText().toString();
                        Log.d(TAG, "onClick: set the password");
                        decryptFile(username,SharedData.KEY_PASSWORD,dbpass,files);
                        dialog.dismiss();
                    }
                }
            });
        } else {
            isCalled=false;
            if(!isOperationNotRunning(files)){
                return;
            }

            SharedData.IS_TASK_CANCELED = false;
            SharedData.CURRENT_RUNNING_OPERATIONS = new ArrayList<>(files);
            mDecryptTask = new DecryptTask(
                    mContext,
                    mAdapter,
                    new ArrayList<>(files),
                    dbpass,
                    username,
                    keypass
            );
            try {
                mDecryptTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (IllegalStateException ex) {
                Toast.makeText(
                        mContext,
                        "Already decrypting files",
                        Toast.LENGTH_LONG
                ).show();

            }
        }
    }

    private boolean continueEncryption=false;
    public void encryptTask(ArrayList<String> files){
        if(SharedData.ASK_ENCRYPTION_CONFIG){
            AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
            builder.setTitle("Encryption");
            builder.setMessage("Do you really want to encrypt the selected files?");
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    continueEncryption=false;
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    continueEncryption=true;
                }
            });
            builder.show();
        }
        if(!continueEncryption){
            return;
        }
        //check if user hasn't generate keys
        if(!SharedData.KEYS_GENERATED){
            //generate keys first
            generateKeys();
            return;
        }

        if(!isOperationNotRunning(files)){
            return;
        }

        SharedData.IS_TASK_CANCELED=false;
        SharedData.CURRENT_RUNNING_OPERATIONS=new ArrayList<>(files);
        mEncryptTask=new EncryptTask(
                mContext,
                mAdapter,
                new ArrayList<>(files)
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
                UiUtils.reloadData(mAdapter);
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
        if(!isOperationNotRunning(mSelectedFiles)){
            return;
        }
        new MoveTask(mContext,mSelectedFiles,dest,m).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    public void compressTask(ArrayList<String> files,boolean uncompress){
        if(!isOperationNotRunning(files)){
            return;
        }
        ArrayList<String> tmp=new ArrayList<>(files);
        SharedData.CURRENT_RUNNING_OPERATIONS=tmp;
        new CompressTask(tmp,mContext,mAdapter,uncompress,mAdapter.getmFileFiller().getCurrentPath()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public ArrayList<String> getmSelectedFiles() {
        return mSelectedFiles;
    }

    public void setmSelectedFiles(ArrayList<String> mSelectedFiles) {
        this.mSelectedFiles = mSelectedFiles;
    }
    private boolean isOperationNotRunning(ArrayList<String> files){
       if(SharedData.checkIfInRunningTask(files)){
            Toast.makeText(mContext,"Another operation is already running on selected files. please wait",Toast.LENGTH_LONG).show();
            return false;
       }
       return true;
    }


}

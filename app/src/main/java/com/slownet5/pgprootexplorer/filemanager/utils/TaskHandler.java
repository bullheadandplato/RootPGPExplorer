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
import android.os.AsyncTask;
import android.widget.Toast;

import com.slownet5.pgprootexplorer.filemanager.listview.FileListAdapter;
import com.slownet5.pgprootexplorer.filemanager.listview.FileSelectionManagement;
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

    private static final String TAG=TaskHandler.class.getCanonicalName();
    protected TaskHandler(Context context,FileListAdapter adapter,FileSelectionManagement m){
        this.mContext               = context;
        mAdapter                    = adapter;
        mFileSelectionManagement    = m;
    }

    protected void renameFile(final String filename,final String destName){

         new RenameTask(
                            mContext,
                            mAdapter,
                            filename,
                            destName

         ).execute();


    }
    protected void deleteFile(final ArrayList<String> files){
        beforeStartingTask(files);
        DeleteTask task=new DeleteTask(
                                mContext,
                                mAdapter,
                                files
                        );
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    protected void decryptFile(final String username, final String keypass, final String dbpass, final ArrayList<String> files) {
           beforeStartingTask(files);
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
    protected void encryptTask(final ArrayList<String> files){
        beforeStartingTask(files);
        mEncryptTask=new EncryptTask(
                mContext,
                mAdapter,
                new ArrayList<>(files)
        );
        mEncryptTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected DecryptTask getDecryptTask() {
        return mDecryptTask;
    }

    protected EncryptTask getEncryptTask() {
        return mEncryptTask;
    }

    protected void moveFiles(String dest,FileListAdapter m,ArrayList<String> files){
        beforeStartingTask(files);
        new MoveTask(mContext,files,dest,m).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    protected void compressTask(ArrayList<String> files,boolean uncompress){
        beforeStartingTask(files);
        new CompressTask(
                files,
                mContext,
                mAdapter,
                uncompress,
                mAdapter.getmFileFiller().getCurrentPath()
        ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private void beforeStartingTask(ArrayList<String> files){
        SharedData.CURRENT_RUNNING_OPERATIONS=files;
        SharedData.IS_TASK_CANCELED=true;
    }



}
